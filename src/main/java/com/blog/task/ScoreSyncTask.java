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
    private static final String POST_SCORE_CHANGE="post:score:changed";
    private static final String POST_ZSET_SCORE="post:score:";
    private static final String COMMENT_COUNT_KEY="comment:count:";//string 存储所有的评论数量
    private static final String POST_LIKE_COUNT="post:like:count:";//String 存储所有点赞数量

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostMapper postMapper;

    @Scheduled(fixedRate = 60000)
    public void syncScore(){
        //获取需要修改分数的文章id
        Set<String> postIds = stringRedisTemplate.opsForSet().members(POST_SCORE_CHANGE);
        if(postIds==null) return;
        //修改
        for(String postId:postIds){
            //key
            String commentCountKey=COMMENT_COUNT_KEY+postId;
            String likeCountKey=POST_LIKE_COUNT+postId;
            //str值
            String likeCountStr=stringRedisTemplate.opsForValue().get(likeCountKey);
            String commentCountStr=stringRedisTemplate.opsForValue().get(commentCountKey);
            //转为Long
            Long likeCount=likeCountStr==null?0:Long.parseLong(likeCountStr);
            Long commentCount=commentCountStr==null?0:Long.parseLong(commentCountStr);
            //计算分数
            Long score=likeCount*2+commentCount*3;
            stringRedisTemplate.opsForZSet().add(POST_ZSET_SCORE,postId,score);
        }
        //清空
        stringRedisTemplate.delete(POST_SCORE_CHANGE);
    }
}
