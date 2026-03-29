package com.blog.controller;

import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.utils.JwtUtil;
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

    @PostMapping("/login")
    public String login(@RequestBody User user){
        userService.login(user.getUsername(),user.getPassword());
        String token= JwtUtil.generateToken(user.getUsername());
        return token ;
    }
}
