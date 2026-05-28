package com.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.PageResult;
import com.campus.entity.HelpPost;
import com.campus.entity.User;
import com.campus.mapper.HelpPostMapper;
import com.campus.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 求助帖子服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HelpPostService {

    private final HelpPostMapper helpPostMapper;
    private final UserMapper userMapper;

    /**
     * 发布帖子
     */
    public void createPost(HelpPost post) {
        post.setViewCount(0);
        post.setReplyCount(0);
        post.setStatus(0); // 正常
        helpPostMapper.insert(post);
    }

    /**
     * 更新帖子
     */
    public void updatePost(HelpPost post) {
        helpPostMapper.updateById(post);
    }

    /**
     * 删除帖子
     */
    public void deletePost(Long id) {
        helpPostMapper.deleteById(id);
    }

    /**
     * 获取帖子详情
     */
    public HelpPost getPostById(Long id) {
        HelpPost post = helpPostMapper.selectById(id);
        if (post != null) {
            // 增加浏览次数
            post.setViewCount(post.getViewCount() + 1);
            helpPostMapper.updateById(post);
        }
        return post;
    }

    /**
     * 分页查询帖子列表
     */
    public PageResult<HelpPost> getPostList(Integer pageNum, Integer pageSize, 
                                             String category, String keyword) {
        Page<HelpPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HelpPost> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(HelpPost::getStatus, 0);
        
        if (StringUtils.hasText(category)) {
            wrapper.eq(HelpPost::getCategory, category);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(HelpPost::getTitle, keyword)
                             .or()
                             .like(HelpPost::getContent, keyword));
        }
        
        wrapper.orderByDesc(HelpPost::getIsTop)
               .orderByDesc(HelpPost::getCreateTime);
        Page<HelpPost> result = helpPostMapper.selectPage(page, wrapper);
        
        PageResult<HelpPost> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 获取我的帖子
     */
    public PageResult<HelpPost> getMyPosts(Long userId, Integer pageNum, Integer pageSize) {
        Page<HelpPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HelpPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HelpPost::getPublisherId, userId)
               .ne(HelpPost::getStatus, 3)
               .orderByDesc(HelpPost::getCreateTime);
        Page<HelpPost> result = helpPostMapper.selectPage(page, wrapper);
        
        PageResult<HelpPost> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    public void markResolved(Long id, Long userId) {
        HelpPost post = helpPostMapper.selectById(id);
        if (post != null && post.getPublisherId().equals(userId)) {
            post.setStatus(1);
            helpPostMapper.updateById(post);
        }
    }

    public void closePost(Long id, Long userId) {
        HelpPost post = helpPostMapper.selectById(id);
        if (post != null && post.getPublisherId().equals(userId)) {
            post.setStatus(2);
            helpPostMapper.updateById(post);
        }
    }

    /**
     * 获取帖子总数
     */
    public long getPostCount() {
        return helpPostMapper.selectCount(null);
    }

    /**
     * 管理端分页查询帖子列表（支持分类、关键词、状态筛选）
     */
    public PageResult<HelpPost> getAdminPostList(Integer pageNum, Integer pageSize,
                                                  String category, String keyword, Integer status) {
        Page<HelpPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HelpPost> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(category)) {
            wrapper.eq(HelpPost::getCategory, category);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(HelpPost::getTitle, keyword);
        }

        if (status != null) {
            wrapper.eq(HelpPost::getStatus, status);
        }

        wrapper.orderByDesc(HelpPost::getCreateTime);
        Page<HelpPost> result = helpPostMapper.selectPage(page, wrapper);

        PageResult<HelpPost> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setRecords(result.getRecords());
        return pageResult;
    }

    /**
     * 审核通过帖子
     */
    public void approvePost(Long id) {
        HelpPost post = helpPostMapper.selectById(id);
        if (post != null) {
            post.setStatus(0);
            helpPostMapper.updateById(post);
        }
    }

    /**
     * 拒绝帖子
     */
    public void rejectPost(Long id) {
        HelpPost post = helpPostMapper.selectById(id);
        if (post != null) {
            post.setStatus(3);
            helpPostMapper.updateById(post);
        }
    }

    /**
     * 按分类统计帖子数量
     */
    public Map<String, Long> getPostCountByCategory() {
        Map<String, Long> result = new HashMap<>();
        List<HelpPost> all = helpPostMapper.selectList(null);
        for (HelpPost p : all) {
            String cat = p.getCategory() != null ? p.getCategory() : "其他";
            result.put(cat, result.getOrDefault(cat, 0L) + 1);
        }
        return result;
    }

    /**
     * 按学院统计用户数量
     */
    public Map<String, Long> getUserCountByCollege() {
        Map<String, Long> result = new HashMap<>();
        List<User> all = userMapper.selectList(null);
        for (User u : all) {
            String college = u.getCollege() != null ? u.getCollege() : "其他";
            result.put(college, result.getOrDefault(college, 0L) + 1);
        }
        return result;
    }
}
