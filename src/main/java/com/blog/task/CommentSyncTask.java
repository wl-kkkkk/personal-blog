package com.blog.task;

import com.blog.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CommentSyncTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostMapper postMapper;

    private static final String COMMENT_COUNT_KEY="comment:count:";//用string存储
    private static final String COMMENT_CHANGED_KEY="comment:changed";//用set存储更新过评论数量的文章id
    //新增一个set用来存放评论和点赞改变的文章id，定时更新这几个文章的score;
    private static final String POST_SCORE_CHANGE="post:score:changed";

    @Scheduled(fixedRate = 300000)
    public void syncComment(){
        //获取修改过的文章id
        Set<String> postIds = stringRedisTemplate.opsForSet().members(COMMENT_CHANGED_KEY);

        //更新数据库
        for(String postId:postIds){
            String commentCountKey=COMMENT_COUNT_KEY+postId;
            Long count= Long.parseLong(stringRedisTemplate.opsForValue().get(commentCountKey));
            postMapper.getById(Long.parseLong(postId)).setCommentCount(count);
        }

        //设置需要更新分数的文章
        for(String postId:postIds){
            stringRedisTemplate.opsForSet().add(POST_SCORE_CHANGE,postId);
        }

        //清空更新评论文章id
        stringRedisTemplate.delete(COMMENT_CHANGED_KEY);
    }
}
