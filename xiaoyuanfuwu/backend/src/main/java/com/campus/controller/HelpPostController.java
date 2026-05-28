package com.campus.controller;

import com.campus.common.Result;
import com.campus.entity.Comment;
import com.campus.entity.HelpPost;
import com.campus.service.CommentService;
import com.campus.service.HelpPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/help")
@RequiredArgsConstructor
public class HelpPostController {

    private final HelpPostService helpPostService;
    private final CommentService commentService;

    /**
     * 发布帖子
     */
    @PostMapping("/publish")
    public Result<Void> publishPost(@RequestBody HelpPost post) {
        try {
            helpPostService.createPost(post);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新帖子
     */
    @PutMapping("/update")
    public Result<Void> updatePost(@RequestBody HelpPost post) {
        try {
            helpPostService.updatePost(post);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        try {
            helpPostService.deletePost(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/detail/{id}")
    public Result<HelpPost> getPostDetail(@PathVariable Long id) {
        HelpPost post = helpPostService.getPostById(id);
        return Result.success(post);
    }

    /**
     * 分页查询帖子列表
     */
    @GetMapping("/list")
    public Result<?> getPostList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) String keyword) {
        try {
            return Result.success(helpPostService.getPostList(pageNum, pageSize, category, keyword));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的帖子
     */
    @GetMapping("/myPosts/{userId}")
    public Result<?> getMyPosts(@PathVariable Long userId,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            return Result.success(helpPostService.getMyPosts(userId, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/resolve/{id}")
    public Result<Void> resolvePost(@PathVariable Long id, @RequestParam Long userId) {
        try {
            helpPostService.markResolved(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/close/{id}")
    public Result<Void> closePost(@PathVariable Long id, @RequestParam Long userId) {
        try {
            helpPostService.closePost(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/comment/add")
    public Result<Void> addComment(@RequestBody Comment comment) {
        try {
            commentService.addComment(comment);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/comment/delete/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/comment/list/{postId}")
    public Result<?> getComments(@PathVariable Long postId,
                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            return Result.success(commentService.getCommentsByPostId(postId, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/comment/replies/{parentId}")
    public Result<?> getReplies(@PathVariable Long parentId) {
        try {
            return Result.success(commentService.getRepliesByParentId(parentId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
