package com.campus.controller;

import com.campus.common.Result;
import com.campus.service.KimiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final KimiService kimiService;

    @PostMapping("/voice-command")
    public Result<Map<String, Object>> processVoiceCommand(
            @RequestBody Map<String, String> request,
            @RequestAttribute(required = false) Long userId) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return Result.error("语音内容不能为空");
        }
        Map<String, Object> result = kimiService.processVoiceCommand(text, userId);
        return Result.success(result);
    }
}
