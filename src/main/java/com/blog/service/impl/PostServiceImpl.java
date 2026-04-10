package com.blog.service.impl;

import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import com.blog.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String HOT_POST_KEY="hot:posts";

    @Override
    public List<Post> list() {
        return postMapper.findAll();
    }

    @Override
    public List<Post> page(int pageNum,int pageSize){
        int offset = (pageNum-1)*pageSize;
        return postMapper.getByPage(offset,pageSize);
    }

    @Override
    public Post getById(Long id) {
        return postMapper.getById(id);
    }

    @Override
    public void add(Post post) {
        postMapper.insert(post);
    }

    @Override
    public void update(Post post) {
        postMapper.update(post);
    }

    @Override
    public void delete(Long id) {
        postMapper.delete(id);
    }

    @Override
    public List<Post> searchByTitle(String keyword){
        return postMapper.searchByTitle(keyword);
    }

    @Override
    public List<Post> getHotPosts(){
        try {
            String json = redisTemplate.opsForValue().get(HOT_POST_KEY);
            ObjectMapper mapper=new ObjectMapper();
            if(json!=null){
                return mapper.readValue(json, new TypeReference<List<Post>>() {
                });
            }
            //Redis没有查mysql
            List<Post> posts = postMapper.selectHotPosts();
            redisTemplate.opsForValue().set(HOT_POST_KEY,mapper.writeValueAsString(posts),5, TimeUnit.MINUTES);
            return posts;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return postMapper.selectHotPosts();
        }
    }

}
