package com.blog.task;

import com.blog.dto.HotPostDTO;
import com.blog.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotPostSyncTask {

    private static final String HOT_POST_KEY="posts:hot:zset";
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 3 * * ?")
    public void hotPostInit(){

        //删除旧的
        stringRedisTemplate.delete(HOT_POST_KEY);

        //获取热门文章
        List<HotPostDTO> hotPosts = postMapper.selectHotPosts();

        if(hotPosts==null||hotPosts.isEmpty()) {
            return;
        }

        //插入redis
        for(HotPostDTO hotPost:hotPosts){
            String postId=hotPost.getPostId().toString();
            Double score=hotPost.getScore();
            stringRedisTemplate.opsForZSet().add(HOT_POST_KEY,postId,score);
        }

    }

    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void onApplicationReady(){
        hotPostInit();
    }
}
