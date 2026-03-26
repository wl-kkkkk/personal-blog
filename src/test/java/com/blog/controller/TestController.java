package com.blog.controller;

import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestController {

    @Autowired
    private PostMapper postMapper;

    @Test
    public void test() {
        List<Post> posts = postMapper.findAll();
        System.out.println("查询结果：" + posts);
    }
}
