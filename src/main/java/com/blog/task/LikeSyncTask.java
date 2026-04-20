package com.blog.task;

import com.blog.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class LikeSyncTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostMapper postMapper;

    private static final String POST_CHANGE_LIKE="post:like:change";//存储修改过点赞数的文章id
    private static final String POST_LIKE_COUNT="post:like:count:";//值为String

    @Scheduled(fixedRate = 300000)
    public void syncLikeCounr(){
        //获取所有被修改的postId
        Set<String> postIdSet=stringRedisTemplate.opsForSet().members(POST_CHANGE_LIKE);
        if(postIdSet==null||postIdSet.isEmpty()) return;

        //获取redis中的点赞数
        for(String postId:postIdSet){
            String countKey=POST_LIKE_COUNT+postId;
            Long likeCount= Long.valueOf(stringRedisTemplate.opsForValue().get(countKey));
            //更新数据库
            postMapper.setPostLikeCount((long) Integer.parseInt(postId),likeCount);
        }

        //清空变更记录
        stringRedisTemplate.delete(POST_CHANGE_LIKE);
    }
}
