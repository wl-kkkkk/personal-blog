package com.blog.service.impl;

import com.blog.mapper.PostLikeMapper;
import com.blog.service.PostLikeService;
import com.blog.utils.UserContext;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostLikeServiceImpl implements PostLikeService {

    @Autowired
    private static PostLikeMapper postLikeMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String POST_LIKE_USER="post:like:user:";//set后更userId，值为postId
    private static final String POST_LIKE_COUNT="post:like:count:";//值为String
    private static final String POST_CHANGE_LIKE="post:like:change";//存储修改过点赞数的文章id

    @Override
    public void like(Long postId) {
        Long userId = UserContext.getUserId();
        String userKey=POST_LIKE_USER+userId;
        String countKey=POST_LIKE_COUNT+postId;

        //查询是否存在于缓存中
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(userKey, postId);
        if(BooleanUtils.isFalse(isMember)){
            //不存在就加入并更新数据库？并更新点赞量
            stringRedisTemplate.opsForSet().add(userKey,postId.toString());
            postLikeMapper.insert(userId,postId);
            stringRedisTemplate.opsForValue().increment(countKey);
        }else{
            //存在就删除，并更新数据库？,并更新点赞量
            stringRedisTemplate.opsForSet().remove(userKey,postId);
            postLikeMapper.delete(userId,postId);
            stringRedisTemplate.opsForValue().decrement(countKey);
        }
        //存储修改过点赞数的文章id，定时任务执行
        stringRedisTemplate.opsForSet().add(POST_CHANGE_LIKE,postId.toString());
    }

}
