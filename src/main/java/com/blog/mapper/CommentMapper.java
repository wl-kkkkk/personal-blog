package com.blog.mapper;

import com.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    void publishComment(Comment comment);
    List<Comment> selectByPostId(Long postId);
    List<Comment> selectByUserId(Long userId);
}
