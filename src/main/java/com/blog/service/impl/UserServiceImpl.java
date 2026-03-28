package com.blog.service.impl;

import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password){
        User user = userMapper.findByUsername(username);
        if(user==null){
            throw new RuntimeException("用户不存在");
        }

        if(!user.getPassword().equals(password)){
            throw new RuntimeException("密码错误");
        }

        return user;
    }
}
