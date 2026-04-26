package com.blog.dto;


public class HotPostDTO {
    private Long postId;
    private Double score;

    public HotPostDTO(){}
    public HotPostDTO(Long postId, Double score) {
        this.postId = postId;
        this.score = score;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
