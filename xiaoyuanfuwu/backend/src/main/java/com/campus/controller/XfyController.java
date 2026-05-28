package com.campus.controller;

import com.campus.common.Result;
import com.campus.service.XfyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/xfy")
@RequiredArgsConstructor
public class XfyController {

    private final XfyService xfyService;

    @PostMapping(value = "/speech-to-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> speechToText(@RequestParam("audio") MultipartFile audio) {
        try {
            byte[] audioData = audio.getBytes();
            String text = xfyService.speechToText(audioData);
            if (text == null || text.trim().isEmpty()) {
                return Result.error("语音识别失败，请重试");
            }
            return Result.success(Map.of("text", text));
        } catch (Exception e) {
            log.error("语音识别接口异常", e);
            return Result.error("语音识别服务异常：" + e.getMessage());
        }
    }

    @PostMapping("/text-to-speech")
    public ResponseEntity<byte[]> textToSpeech(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] audioData = xfyService.textToSpeech(text);
            if (audioData == null || audioData.length == 0) {
                log.error("语音合成返回空数据");
                return ResponseEntity.status(500).build();
            }

            log.info("语音合成成功，返回音频大小: {} bytes", audioData.length);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.setContentLength(audioData.length);
            return ResponseEntity.ok().headers(headers).body(audioData);
        } catch (Exception e) {
            log.error("语音合成接口异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
