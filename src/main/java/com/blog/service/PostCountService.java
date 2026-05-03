package com.blog.service;

import com.blog.dto.PostDetailDTO;
import com.blog.entity.Post;

import java.util.List;

public interface PostCountService {

    void fillPost(Post post);
    void fillPost(PostDetailDTO post);
    void fillPosts(List<Post> posts);
}
