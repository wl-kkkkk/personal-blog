package com.blog.controller;

import com.blog.entity.Comment;
import com.blog.service.CommentService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PutMapping("/add")
    public Result publishComment(@RequestBody Comment comment){
        commentService.publishComment(comment);
        return Result.success();
    }

    @GetMapping("/all")
    public Result<List<Comment>> getByPostId(Long postId){
        return Result.success(commentService.selectByPostId(postId));
    }

    @GetMapping("/list")
    public Result<List<Comment>> listByPostId(Long postId){
        return Result.success(commentService.listByPostId(postId));
    }

    @GetMapping("/userComments")
    public Result<List<Comment>> getCommentsByUserId(@RequestParam Long userId){
        return Result.success(commentService.selectByUserId(userId));
    }
}
