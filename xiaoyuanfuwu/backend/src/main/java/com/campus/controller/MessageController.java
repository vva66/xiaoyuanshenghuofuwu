package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Message;
import com.campus.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public Result<Void> sendMessage(@RequestBody Message message) {
        try {
            messageService.sendMessage(message);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/conversations")
    public Result<?> getConversations(@RequestParam Long userId,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            return Result.success(messageService.getConversationList(userId, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/conversation")
    public Result<List<Message>> getConversation(@RequestParam Long userId,
                                                  @RequestParam Long otherUserId,
                                                  @RequestParam Long relatedPostId) {
        try {
            List<Message> messages = messageService.getConversation(userId, otherUserId, relatedPostId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/unread")
    public Result<Map<String, Long>> getUnreadCount(@RequestParam Long userId) {
        try {
            long count = messageService.getUnreadCount(userId);
            return Result.success(Map.of("count", count));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/read")
    public Result<Void> markAsRead(@RequestParam Long userId,
                                    @RequestParam Long otherUserId,
                                    @RequestParam Long relatedPostId) {
        try {
            messageService.markAsRead(userId, otherUserId, relatedPostId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/readAll")
    public Result<Void> markAllAsRead(@RequestParam Long userId) {
        try {
            messageService.markAllAsRead(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
