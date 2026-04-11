package com.blog.controller;

import com.blog.annotation.NoAuth;
import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import com.blog.service.PostService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.blog.utils.Result.success;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/list")
    public Result<List<Post>> findAll(){
        return Result.success(postService.list());
    }

    @GetMapping("/page")
    public Result<List<Post>> getByPage(@RequestParam int pageNum,@RequestParam int pageSize){
        return Result.success(postService.page(pageNum,pageSize));
    }

    @GetMapping("/{id}")
    public Post getById(@PathVariable Long id){
        return postService.getById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Post post){
        postService.add(post);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Post post){
        postService.update(post);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        postService.delete(id);
        return Result.success();
    }

    @GetMapping("/search")
    public Result<List<Post>> searchByTitle(@RequestParam String keyword){
        return Result.success(postService.searchByTitle(keyword));
    }

    @GetMapping("/hot")
    public Result<List<Post>> getHotPosts(){
        List<Post> hotposts=postService.getHotPosts();
        return Result.success(hotposts);
    }

}
