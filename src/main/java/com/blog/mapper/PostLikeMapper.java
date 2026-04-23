package com.blog.mapper;
import com.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostLikeMapper {
    boolean insert(@Param("userId")Long userId, @Param("postId") Long postId, @Param("createTime") LocalDateTime createTime);
    boolean delete(@Param("userId")Long userId,@Param("postId") Long postId);
    boolean isLike(@Param("userId")Long userId,@Param("postId") Long postId);
    List<User> selectUsersByPost(Long postId);
}
