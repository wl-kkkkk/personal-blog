package com.blog.entity;

import java.time.LocalDateTime;

public class PostLike {
    private Long id;
    private Long userId;
    private Long postId;
    private LocalDateTime createTime;

    public PostLike(){}
    public PostLike(Long id, Long userId, LocalDateTime createTime, Long postId) {
        this.id = id;
        this.userId = userId;
        this.createTime = createTime;
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "PostLike{" +
                "id=" + id +
                ", userId=" + userId +
                ", postId=" + postId +
                ", createTime=" + createTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
