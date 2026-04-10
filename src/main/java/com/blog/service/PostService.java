package com.blog.service;

import com.blog.entity.Post;

import java.util.List;

public interface PostService {
    List<Post> list();
    List<Post> page(int pageNum,int pageSize);
    Post getById(Long id);
    void add(Post post);
    void update(Post post);
    void delete(Long id);
    List<Post> searchByTitle(String keyword);
    List<Post> getHotPosts();
}
