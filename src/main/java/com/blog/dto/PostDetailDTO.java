package com.blog.dto;

import java.util.Date;

public class PostDetailDTO {

    private Long id;
    private String title;
    private String context;
    private Long userId;
    private Date createTime;
    private Long likeCount;
    private Long commentCount;
    private String authorName;

    public PostDetailDTO(){}

    public PostDetailDTO(Long id, String title, String context, Long userId, Date createTime, Long likeCount, Long commentCount, String authorName) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.userId = userId;
        this.createTime = createTime;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.authorName = authorName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
