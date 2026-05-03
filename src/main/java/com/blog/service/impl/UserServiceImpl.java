package com.blog.service.impl;

import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
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

        if(!BCrypt.checkpw(password,user.getPassword())){
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    @Override
    public void register(User user){

        if(user.getPassword()==null || user.getUsername()==null){
            throw new RuntimeException("参数不能为空");
        }

        String username=user.getUsername();
        
        User exist = userMapper.findByUsername(username);
        if(exist!=null){
            throw new RuntimeException("用户名已存在");
        }

        String password = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt());
        userMapper.insert(username,password);
    }
}


