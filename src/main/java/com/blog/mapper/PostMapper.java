package com.blog.mapper;

import com.blog.dto.HotPostDTO;
import com.blog.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface PostMapper {
    List<Post> findAll();
    void insert(Post post);
    void update(Post post);
    void delete(Long id);

    /*
    * 通过id获得某个文章
    * service层：searchByUserId调用
    * */
    Post getById(Long id);

    /*
     * 获取某个用户的所有文章
     * controller层：getPostsByUserId
     * service层：searchByUserId
     * map层：searchByUserId
     * */
    List<Long> searchByUserId(Long userId);
    List<Post> getByPage(@Param("offset") int offset,@Param("size") int size);
    List<Post> searchByTitle(String keyword);

    Long setPostLikeCount(@Param("id") Long id,@Param("likeCount") Long likeCount);
    List<HotPostDTO> selectHotPosts();
    List<Post> selectPostsByIds(Set<String> postIds);

}
