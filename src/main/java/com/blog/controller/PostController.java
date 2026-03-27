package com.blog.controller;

import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import com.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/list")
    public List<Post> findAll(){
        return postService.list();
    }

    @GetMapping("/{id}")
    public Post getById(@PathVariable Long id){
        return postService.getById(id);
    }

    @PostMapping("/add")
    public String add(@RequestBody Post post){
        postService.add(post);
        return "success";
    }

    @PutMapping("/update")
    public String update(@RequestBody Post post){
        postService.update(post);
        return "success";
    }

    @DeleteMapping("/{id}")
    public String update(@PathVariable Long id){
        postService.delete(id);
        return "success";
    }
}
