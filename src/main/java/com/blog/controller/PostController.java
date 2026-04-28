package com.blog.controller;

import com.blog.annotation.NoAuth;
import com.blog.entity.Post;
import com.blog.service.PostService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/list")
    public Result<List<Post>> findAll(){
        return Result.success(postService.list());
    }

    /*
     * 分页查询近20天的所有文章
     * controller：getByPage
     * service:page
     * mapper:getByPage
     * */
    @GetMapping("/page")
    public Result<List<Post>> getByPage(@RequestParam int pageNum,@RequestParam int pageSize){
        return Result.success(postService.page(pageNum,pageSize));
    }

    /*
     * 通过id获取文章详细内容
     * controller层：getById
     * service层：Post getById
     * mapper:getById
     * */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        return Result.success(postService.getById(id));
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

    /*
    * 根据输入的内容模糊查询文章
    * controller:searchByTitle
    * service:searchByTitle
    * mapper:searchByTitle
    * */
    @GetMapping("/search")
    public Result<List<Post>> searchByTitle(@RequestParam String keyword){
        return Result.success(postService.searchByTitle(keyword));
    }

    /*
    * 获取热榜
    * controller层：getHotPosts
    * service层：getHotPosts
    * mapper层：selectPostsById(postIds)
    * */
    @GetMapping("/hot")
    public Result<List<Post>> getHotPosts(){
        List<Post> hotposts=postService.getHotPosts();
        return Result.success(hotposts);
    }

    /*
    * 获取某个用户的所有文章
    * controller层：getPostsByUserId
    * service层：searchByUserId
    * map层：searchByUserId
    * */
    @GetMapping("/userPost")
    public Result<List<Post>> getPostsByUserId(@RequestParam Long userId){
        List<Post> posts=postService.searchByUserId(userId);
        return Result.success(posts);
    }


}
