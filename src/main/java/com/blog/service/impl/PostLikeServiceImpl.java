package com.blog.service.impl;

import com.blog.service.PostLikeService;
import com.blog.utils.UserContext;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostLikeServiceImpl implements PostLikeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String POST_LIKE_USER="post:like:user";//set 后跟postId,值为userId，存储列表
    private static final String POST_LIKE_COUNT="post:like:count:";//值为String
    private static final String POST_CHANGE_LIKE="post:like:change";//存储修改过点赞数的文章id
    private static final String USER_CHANGE_LIKE="post:user:change:";//hash 后跟postId，fileduserId,value1or-1
    private static final String HOT_POST_KEY="posts:hot:zset";

    @Override
    public void like(Long postId) {
        Long userId = UserContext.getUserId();
        String userKey=POST_LIKE_USER+userId;
        String countKey=POST_LIKE_COUNT+postId;
        String userChangeKey=USER_CHANGE_LIKE+postId;

        //查询是否存在于缓存中
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(userKey, userId);
        if(BooleanUtils.isFalse(isMember)){
            //不存在(没点赞)就加入喜爱列表&&存入改变的用户hash表中&&更新点赞量
            stringRedisTemplate.opsForSet().add(userKey,userId.toString());
            stringRedisTemplate.opsForHash().put(userChangeKey,userId,"1");
            stringRedisTemplate.opsForValue().increment(countKey);

            //实时更新热榜+最终一致性
            stringRedisTemplate.opsForZSet().incrementScore(HOT_POST_KEY,postId.toString(),2);
        }else{
            //存在就删除喜爱列表中对应的键值对&&存入改变的用户hash表中&&更新点赞量
            stringRedisTemplate.opsForSet().remove(userKey,userId);
            stringRedisTemplate.opsForHash().put(userChangeKey,userId,"0");
            stringRedisTemplate.opsForValue().decrement(countKey);

            //实时更新热榜+最终一致性
            stringRedisTemplate.opsForZSet().incrementScore(HOT_POST_KEY,postId.toString(),-2);
        }
        //存储修改过点赞数的文章id，定时任务执行
        stringRedisTemplate.opsForSet().add(POST_CHANGE_LIKE,postId.toString());
    }

}
