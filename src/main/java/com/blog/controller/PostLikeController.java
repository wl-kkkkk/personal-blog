package com.blog.controller;

import com.blog.service.PostLikeService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostLikeController {

    @Autowired
    private PostLikeService postLikeService;

    @PutMapping("/like")
    public Result<Long> like(@RequestParam Long postId){
        return Result.success(postLikeService.like(postId));
    }
}
