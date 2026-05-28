package com.campus.service;

import com.campus.common.Result;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KimiService {

    @Value("${kimi.api-key:}")
    private String apiKey;

    @Value("${kimi.api-url:https://api.moonshot.cn/v1/chat/completions}")
    private String apiUrl;

    @Value("${kimi.model:kimi-k2.5}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public Map<String, Object> processVoiceCommand(String text, Long userId) {
        try {
            String systemPrompt = buildSystemPrompt(userId);
            String userPrompt = "用户说：" + text;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", 1.0);
            requestBody.put("max_tokens", 1024);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userPrompt));
            requestBody.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String content = root.path("choices").get(0).path("message").path("content").asText();
                return parseResponse(content);
            } else {
                log.error("Kimi API error: {}", response.getStatusCode());
                return createErrorResult("AI服务暂时不可用，请稍后再试");
            }
        } catch (Exception e) {
            log.error("Kimi API call failed", e);
            return createErrorResult("AI服务连接失败，请稍后再试");
        }
    }

    private String buildSystemPrompt(Long userId) {
        return """
            你是一个校园智能语音助手，运行在校园服务系统中。
            用户的ID是：%s

            你的任务是根据用户的语音指令，判断用户的意图，并返回结构化的JSON结果。

            ## 可用的意图列表：
            1. publish_secondhand - 发布二手交易
            2. publish_lost_found - 发布失物招领
            3. publish_help - 发布学习求助
            4. view_schedule - 查看课表
            5. view_activities - 查看活动列表
            6. view_canteen - 查看食堂推荐
            7. view_clubs - 查看社团列表
            8. view_help_square - 查看互助广场
            9. view_profile - 查看个人中心
            10. go_home - 返回首页
            11. unknown - 无法识别的指令

            ## 返回格式（必须是纯JSON，不要包含其他文字）：
            {
                "intent": "意图名称",
                "params": {
                    "title": "提取的标题（如果有）",
                    "category": "提取的分类（如果有）",
                    "content": "提取的内容描述（如果有）",
                    "price": "提取的价格（如果有）"
                },
                "reply": "你对用户的友好回复，说明你要帮他做什么"
            }

            ## 示例：
            用户说："我要卖一台笔记本电脑"
            返回：{"intent":"publish_secondhand","params":{"title":"出售笔记本电脑","category":"二手交易","content":"出售一台笔记本电脑","price":""},"reply":"好的，已为您准备好发布二手交易页面，请补充详细信息后发布"}

            用户说："今天有什么课"
            返回：{"intent":"view_schedule","params":{},"reply":"正在为您打开课表页面"}

            用户说："有什么好吃的推荐"
            返回：{"intent":"view_canteen","params":{},"reply":"正在为您打开食堂推荐页面"}

            用户说："我丢了钱包"
            返回：{"intent":"publish_lost_found","params":{"title":"失物招领","category":"失物招领","content":"丢失了物品"},"reply":"已为您打开失物招领发布页面，请填写详细信息"}

            用户说："你好"
            返回：{"intent":"unknown","params":{},"reply":"你好！我是校园智能语音助手，你可以对我说「我要卖东西」「查课表」「推荐食堂」「有什么活动」等指令"}
            """.formatted(userId);
    }

    private Map<String, Object> parseResponse(String content) {
        try {
            content = content.trim();
            if (content.startsWith("```")) {
                content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            }
            JsonNode node = objectMapper.readTree(content);
            Map<String, Object> result = new HashMap<>();
            result.put("intent", node.path("intent").asText("unknown"));
            result.put("reply", node.path("reply").asText(""));
            if (node.has("params")) {
                result.put("params", objectMapper.convertValue(node.path("params"), Map.class));
            } else {
                result.put("params", new HashMap<>());
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to parse Kimi response: {}", content, e);
            return createErrorResult("抱歉，我没有理解您的意思，请换个说法试试");
        }
    }

    private Map<String, Object> createErrorResult(String reply) {
        Map<String, Object> result = new HashMap<>();
        result.put("intent", "unknown");
        result.put("reply", reply);
        result.put("params", new HashMap<>());
        return result;
    }
}
