package com.blog.init;

import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import com.blog.utils.RedisData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CacheInitializer {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String HOT_POST_KEY="hot:posts";

    @PostConstruct
    public void initHotPostCache(){

        //从数据库查询热点文章并放到redis里
        List<Post> hotPosts=postMapper.selectHotPosts();

        RedisData redisData=new RedisData(hotPosts, LocalDateTime.now().plusHours(24L));
        try {
            stringRedisTemplate.opsForValue()
                    .set(HOT_POST_KEY,objectMapper.writeValueAsString(redisData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("未成功写入cache");
        }

    }
}
