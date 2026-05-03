package com.blog.service;

import com.blog.entity.User;


public interface UserService {

    User login(String username,String password);

    void register(User user);

}
