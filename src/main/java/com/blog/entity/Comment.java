package com.blog.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment {
    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private Long parentId;
    private Date createTime;

    //新增字段
    private List<Comment> children=new ArrayList<>();

    public Comment() {
    }

    public Comment(Long id, Date createTime, Long parentId, String content, Long postId, Long userId) {
        this.id = id;
        this.createTime = createTime;
        this.parentId = parentId;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", postId=" + postId +
                ", content='" + content + '\'' +
                ", parentId=" + parentId +
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }
}