package com.blog.service;

import com.blog.mapper.PostLikeMapper;

public interface PostLikeService {
    Long like(Long postId);
}
