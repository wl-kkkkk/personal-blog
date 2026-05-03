package com.blog.controller;

import com.blog.annotation.NoAuth;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.utils.JwtUtil;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 用户登录
     * 使用用户名和密码进行登录认证
     */
    @NoAuth
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        User dbUser=userService.login(user.getUsername(),user.getPassword());
        String token= JwtUtil.generateToken(dbUser.getId());
        return Result.success(token) ;
    }

    @NoAuth
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        userService.register(user);
        return Result.success();
    }

}
