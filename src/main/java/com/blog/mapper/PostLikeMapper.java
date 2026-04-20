package com.blog.mapper;
import org.apache.ibatis.annotations.Param;

public interface PostLikeMapper {
    boolean insert(@Param("userId")Long userId,@Param("postId") Long postId);
    boolean delete(@Param("userId")Long userId,@Param("postId") Long postId);
    boolean isLike(@Param("userId")Long userId,@Param("postId") Long postId);
}
