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
    public Result like(@RequestParam Long postId){
        postLikeService.like(postId);
        return Result.success();
    }
}
