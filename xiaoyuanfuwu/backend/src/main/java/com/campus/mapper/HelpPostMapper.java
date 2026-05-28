package com.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.HelpPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface HelpPostMapper extends BaseMapper<HelpPost> {

    @Update("UPDATE help_post SET reply_count = reply_count + #{delta} WHERE id = #{postId}")
    void updateReplyCount(Long postId, int delta);
}
