package com.blog.service.impl;

import com.blog.entity.Comment;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentService;
import com.blog.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public void publishComment(Comment comment) {

        Long userId= UserContext.getUserId();
        comment.setUserId(userId);
        if(comment.getParentId()==null) comment.setParentId(0L);

        commentMapper.publishComment(comment);
    }

    @Override
    public List<Comment> selectByPostId(Long postId) {
        return commentMapper.selectByPostId(postId);
    }

    @Override
    public List<Comment> listByPostId(Long postId){
        //我决定用hashmap放所有评论去查找而不是一个一个找
        Map<Long,Comment> hashmap=new HashMap<>();
        List<Comment> allComments=commentMapper.selectByPostId(postId);
        for(Comment comment:allComments){
            hashmap.put(comment.getId(),comment);
        }

        //根评论
        List<Comment> roots=new ArrayList<>();

        //实现,类似邻接表
        for(Comment comment:allComments){
            if(comment.getParentId()==null||comment.getParentId()==0) roots.add(comment);
            else{
                Comment parent=hashmap.get(comment.getParentId());
                parent.getChildren().add(comment);
            }
        }
        return roots;
    }
}
