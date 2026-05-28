package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.entity.Message;
import com.campus.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    public void sendMessage(Message message) {
        message.setType(1);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
    }

    public PageResult<Message> getConversationList(Long userId, Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getType, 1)
               .and(w -> w.eq(Message::getSenderId, userId)
                          .or()
                          .eq(Message::getUserId, userId))
               .orderByDesc(Message::getCreateTime);
        Page<Message> result = messageMapper.selectPage(page, wrapper);
        PageResult<Message> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    public List<Message> getConversation(Long userId, Long otherUserId, Long relatedPostId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getType, 1)
               .and(w -> w.and(w2 -> w2.eq(Message::getSenderId, userId)
                                        .eq(Message::getUserId, otherUserId))
                          .or(w2 -> w2.eq(Message::getSenderId, otherUserId)
                                      .eq(Message::getUserId, userId)))
               .eq(Message::getRelatedId, relatedPostId)
               .orderByAsc(Message::getCreateTime);
        return messageMapper.selectList(wrapper);
    }

    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getType, 1)
               .eq(Message::getUserId, userId)
               .eq(Message::getIsRead, 0);
        return messageMapper.selectCount(wrapper);
    }

    public void markAsRead(Long userId, Long otherUserId, Long relatedPostId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getType, 1)
               .eq(Message::getUserId, userId)
               .eq(Message::getSenderId, otherUserId)
               .eq(Message::getRelatedId, relatedPostId)
               .eq(Message::getIsRead, 0);
        Message update = new Message();
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());
        messageMapper.update(update, wrapper);
    }

    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getType, 1)
               .eq(Message::getUserId, userId)
               .eq(Message::getIsRead, 0);
        Message update = new Message();
        update.setIsRead(1);
        update.setReadTime(LocalDateTime.now());
        messageMapper.update(update, wrapper);
    }
}
