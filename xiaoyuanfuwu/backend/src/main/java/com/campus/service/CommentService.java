package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.entity.Comment;
import com.campus.mapper.CommentMapper;
import com.campus.mapper.HelpPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final HelpPostMapper helpPostMapper;

    public void addComment(Comment comment) {
        comment.setLikeCount(0);
        comment.setStatus(1);
        commentMapper.insert(comment);
        helpPostMapper.updateReplyCount(comment.getPostId(), 1);
    }

    public void deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setStatus(2);
            commentMapper.updateById(comment);
            helpPostMapper.updateReplyCount(comment.getPostId(), -1);
        }
    }

    public PageResult<Comment> getCommentsByPostId(Long postId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .eq(Comment::getStatus, 1)
               .orderByAsc(Comment::getCreateTime);
        Page<Comment> result = commentMapper.selectPage(page, wrapper);

        PageResult<Comment> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    public List<Comment> getRepliesByParentId(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
               .eq(Comment::getStatus, 1)
               .orderByAsc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }
}
