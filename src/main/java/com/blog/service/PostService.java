package com.blog.service;

import com.blog.dto.PostDetailDTO;
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

    /*
     * 获取某个用户的所有文章
     * controller层：getPostsByUserId
     * service层：searchByUserId
     * map层：searchByUserId
     * */
    List<Post> searchByUserId(Long userId);

    PostDetailDTO getPostDetailById(Long postId);

    List<Post> getHotPosts();
/*    void likeBlog(Long id);*/
}
