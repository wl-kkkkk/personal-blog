package com.blog.service;

import com.blog.entity.Post;

import java.util.List;

public interface PostService {
    List<Post> list();
    Post getById(Long id);
    void add(Post post);
    void update(Post post);
    void delete(Long id);
}
