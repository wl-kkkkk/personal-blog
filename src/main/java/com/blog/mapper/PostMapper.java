package com.blog.mapper;

import com.blog.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<Post> findAll();
    void insert(Post post);
    void update(Post post);
    void delete(Long id);
    Post getById(Long id);
    List<Post> getByPage(@Param("offset") int offset,@Param("size") int size);
    List<Post> searchByTitle(String keyword);
    List<Post> selectHotPosts();
}
