import { ref, onUnmounted } from 'vue'
import request from '../utils/request'

export function useSpeechRecognition() {
  const isListening = ref(false)
  const isProcessing = ref(false)
  const transcript = ref('')
  const reply = ref('')
  const showReply = ref(false)
  const error = ref('')
  const muted = ref(localStorage.getItem('voiceMuted') === 'true')

  let mediaRecorder = null
  let audioChunks = []
  let audioContext = null
  let audioPlayer = null

  function toggleMute() {
    muted.value = !muted.value
    localStorage.setItem('voiceMuted', muted.value)
    if (muted.value && audioPlayer) {
      audioPlayer.pause()
      audioPlayer.src = ''
    }
  }

  function speak(text) {
    if (muted.value) return
    if (!audioPlayer) {
      audioPlayer = new Audio()
    }
    audioPlayer.pause()
    audioPlayer.currentTime = 0

    const token = localStorage.getItem('token')
    const headers = { 'Content-Type': 'application/json' }
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    fetch('/api/xfy/text-to-speech', {
      method: 'POST',
      headers: headers,
      body: JSON.stringify({ text })
    })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }
      return response.blob()
    })
    .then(blob => {
      const url = URL.createObjectURL(blob)
      audioPlayer.src = url
      audioPlayer.play().catch(e => {
        console.error('语音播放失败:', e)
      })
      audioPlayer.onended = () => {
        URL.revokeObjectURL(url)
      }
    })
    .catch(e => {
      console.error('语音合成请求失败:', e)
    })
  }

  async function startListening() {
    if (isListening.value || isProcessing.value) return

    reply.value = ''
    showReply.value = false
    error.value = ''

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      audioChunks = []

      mediaRecorder = new MediaRecorder(stream, {
        mimeType: MediaRecorder.isTypeSupported('audio/webm;codecs=opus')
          ? 'audio/webm;codecs=opus'
          : 'audio/webm'
      })

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunks.push(event.data)
        }
      }

      mediaRecorder.onstop = async () => {
        stream.getTracks().forEach(track => track.stop())

        const audioBlob = new Blob(audioChunks, { type: mediaRecorder.mimeType })
        audioChunks = []

        if (audioBlob.size < 100) {
          error.value = '未检测到语音，请重试'
          return
        }

        await sendAudioToXfy(audioBlob)
      }

      mediaRecorder.onerror = () => {
        error.value = '录音设备出错'
        isListening.value = false
        stream.getTracks().forEach(track => track.stop())
      }

      mediaRecorder.start()
      isListening.value = true
    } catch (e) {
      if (e.name === 'NotAllowedError' || e.name === 'PermissionDeniedError') {
        error.value = '麦克风权限被拒绝，请在浏览器设置中允许麦克风访问'
      } else if (e.name === 'NotFoundError') {
        error.value = '未检测到麦克风设备，请检查麦克风连接'
      } else {
        error.value = '启动录音失败：' + e.message
      }
    }
  }

  function stopListening() {
    if (mediaRecorder && mediaRecorder.state === 'recording') {
      mediaRecorder.stop()
    }
    isListening.value = false
  }

  async function sendAudioToXfy(audioBlob) {
    isProcessing.value = true
    isListening.value = false

    try {
      const pcmBlob = await convertToPcm(audioBlob)

      const formData = new FormData()
      formData.append('audio', pcmBlob, 'recording.pcm')

      const data = await request.post('/xfy/speech-to-text', formData)

      const text = data.text
      if (!text || !text.trim()) {
        error.value = '语音识别失败，请重试'
        isProcessing.value = false
        return
      }

      transcript.value = text
      await processCommand(text)
    } catch (e) {
      console.error('语音识别请求失败:', e)
      if (e.response) {
        error.value = `服务器错误: ${e.response.status} - ${e.response.data?.message || '未知错误'}`
      } else if (e.request) {
        error.value = '无法连接到服务器，请确认后端已启动'
      } else {
        error.value = `请求出错: ${e.message}`
      }
    } finally {
      isProcessing.value = false
    }
  }

  async function convertToPcm(audioBlob) {
    if (!audioContext) {
      audioContext = new (window.AudioContext || window.webkitAudioContext)({
        sampleRate: 16000
      })
    }

    const arrayBuffer = await audioBlob.arrayBuffer()
    const audioBuffer = await audioContext.decodeAudioData(arrayBuffer)

    const originalSampleRate = audioBuffer.sampleRate
    const targetSampleRate = 16000
    const channelData = audioBuffer.getChannelData(0)
    const targetLength = Math.round(channelData.length * targetSampleRate / originalSampleRate)

    const resampledData = new Float32Array(targetLength)
    for (let i = 0; i < targetLength; i++) {
      const originalIndex = i * originalSampleRate / targetSampleRate
      const floorIndex = Math.floor(originalIndex)
      const frac = originalIndex - floorIndex
      if (floorIndex + 1 < channelData.length) {
        resampledData[i] = channelData[floorIndex] * (1 - frac) + channelData[floorIndex + 1] * frac
      } else {
        resampledData[i] = channelData[floorIndex] || 0
      }
    }

    const pcmData = new Int16Array(targetLength)
    for (let i = 0; i < targetLength; i++) {
      const s = Math.max(-1, Math.min(1, resampledData[i]))
      pcmData[i] = s < 0 ? s * 0x8000 : s * 0x7FFF
    }

    return new Blob([pcmData.buffer], { type: 'audio/pcm' })
  }

  async function processCommand(text) {
    isProcessing.value = true
    try {
      const lowerText = text.toLowerCase()

      let intent = 'unknown'
      let params = {}
      let replyText = ''

      if (/我要卖|出售|转卖|二手|卖出/.test(lowerText)) {
        intent = 'publish_secondhand'
        params = { category: '二手交易', title: '', content: '' }
        replyText = '好的，已为您准备好发布二手交易页面，请补充详细信息后发布'
      } else if (/失物招领|丢了|丢失|遗失|捡到|找到/.test(lowerText)) {
        intent = 'publish_lost_found'
        params = { category: '失物招领', title: '失物招领', content: '丢失了物品' }
        replyText = '已为您打开失物招领发布页面，请填写详细信息'
      } else if (/求助|帮助|辅导|家教|学习|作业|题目/.test(lowerText)) {
        intent = 'publish_help'
        params = { category: '学习求助', title: '', content: '' }
        replyText = '已为您打开学习求助发布页面'
      } else if (/课表|课程|上课|今天.*课|星期.*课/.test(lowerText)) {
        intent = 'view_schedule'
        replyText = '正在为您打开课表页面'
      } else if (/活动|社团活动|校园活动|有什么活动/.test(lowerText)) {
        intent = 'view_activities'
        replyText = '正在为您打开活动列表页面'
      } else if (/食堂|吃饭|吃什么|推荐.*吃|美食|餐厅/.test(lowerText)) {
        intent = 'view_canteen'
        replyText = '正在为您打开食堂推荐页面'
      } else if (/社团|加入社团|社团列表/.test(lowerText)) {
        intent = 'view_clubs'
        replyText = '正在为您打开社团列表页面'
      } else if (/互助|互助广场|求助广场/.test(lowerText)) {
        intent = 'view_help_square'
        replyText = '正在为您打开互助广场页面'
      } else if (/个人中心|我的|个人主页|个人信息|个人资料/.test(lowerText)) {
        intent = 'view_profile'
        replyText = '正在为您打开个人中心页面'
      } else if (/首页|返回首页|主页|回到首页/.test(lowerText)) {
        intent = 'go_home'
        replyText = '正在返回首页'
      } else {
        replyText = '你好！我是校园智能语音助手，你可以对我说「我要卖东西」「查课表」「推荐食堂」「有什么活动」等指令'
      }

      reply.value = replyText
      showReply.value = true
      speak(replyText)
      handleIntent(intent, params)
    } finally {
      isProcessing.value = false
    }
  }

  function handleIntent(intent, params) {
    const router = window.__router
    if (!router) return

    switch (intent) {
      case 'publish_secondhand':
      case 'publish_lost_found':
      case 'publish_help':
        router.push({
          path: '/help/publish',
          query: {
            category: params.category || '二手交易',
            title: params.title || '',
            content: params.content || ''
          }
        })
        break
      case 'view_schedule':
        router.push('/course/schedule')
        break
      case 'view_activities':
        router.push('/activity/list')
        break
      case 'view_canteen':
        router.push('/canteen/list')
        break
      case 'view_clubs':
        router.push('/club/list')
        break
      case 'view_help_square':
        router.push('/help/list')
        break
      case 'view_profile':
        router.push('/profile')
        break
      case 'go_home':
        router.push('/home')
        break
      default:
        break
    }
  }

  onUnmounted(() => {
    stopListening()
    if (audioPlayer) {
      audioPlayer.pause()
      audioPlayer.src = ''
    }
    if (audioContext) {
      audioContext.close()
    }
  })

  return {
    isListening,
    isProcessing,
    transcript,
    reply,
    showReply,
    error,
    muted,
    startListening,
    stopListening,
    speak,
    toggleMute
  }
}
