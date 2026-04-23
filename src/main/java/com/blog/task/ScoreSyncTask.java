package com.blog.task;

import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ScoreSyncTask {
    //新增一个set用来存放评论和点赞改变的文章id，定时更新这几个文章的score;
    //TODO 放更改文章还没有做
    private static final String POST_SCORE_CHANGE="post:score:changed";
    private static final String POST_ZSET_SCORE="post:score:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostMapper postMapper;

    @Scheduled(fixedRate = 300000)
    public void syncScore(){
        //获取需要修改分数的文章id
        Set<String> postIds = stringRedisTemplate.opsForSet().members(POST_SCORE_CHANGE);
        if(postIds==null) return;
        //修改
        for(String postId:postIds){
            Post post=postMapper.getById(Long.parseLong(postId));
            Long score=post.getLikeCount()*2+post.getCommentCount()*3;
            stringRedisTemplate.opsForZSet().add(POST_ZSET_SCORE,postId,score);
        }
    }
}
