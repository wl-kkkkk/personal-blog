package com.blog.service;

import com.blog.entity.Comment;

import java.util.List;

public interface CommentService {
    void publishComment(Comment comment);
    List<Comment> selectByPostId(Long PostId);
    List<Comment> listByPostId(Long PostId);
}
