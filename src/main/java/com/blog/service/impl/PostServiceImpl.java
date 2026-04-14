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
    private static final String POST_VIEW_KEY="posts:view:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
        Post post =postMapper.getById(id);
        if(post!=null){
            String count = stringRedisTemplate.opsForValue().get(POST_VIEW_KEY + id);
            post.setViewCount(id);
        }
        return post;
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

    @Override
    public void incrementViewCount(Long id){
        //先更新缓存（高频读写场景）
        redisTemplate.opsForValue().increment(POST_VIEW_KEY+id);
    }


}
