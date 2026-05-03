package com.blog.mapper;

import com.blog.dto.HotPostDTO;
import com.blog.dto.PostDetailDTO;
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
    * 通过id获得某个文章详细内容
    * service层：searchByUserId调用 getById调用 publishComment调用
    * mapper:getById
    * */
    Post getById(Long id);

    PostDetailDTO getPostDetailById(Long postId);

    /*
     * 获取某个用户的所有文章
     * controller层：getPostsByUserId
     * service层：searchByUserId
     * map层：searchByUserId
     * */
    List<Long> searchByUserId(Long userId);
    List<Post> getByPage(@Param("offset") int offset,@Param("size") int size);
    List<Post> searchByTitle(String keyword);

    List<HotPostDTO> selectHotPosts();
    List<Post> selectPostsByIds(Set<String> postIds);


}
