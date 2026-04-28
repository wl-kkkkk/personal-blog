package com.blog.task;

import com.blog.entity.Post;
import com.blog.mapper.PostLikeMapper;
import com.blog.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Component
public class LikeSyncTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostLikeMapper postLikeMapper;

    //新增一个set用来存放评论和点赞改变的文章id，定时更新这几个文章的score;
    private static final String POST_SCORE_CHANGE="post:score:changed";
    private static final String POST_CHANGE_LIKE="post:like:change";//存储修改过点赞数的文章id
    private static final String POST_LIKE_COUNT="post:like:count:";//值为String
    private static final String USER_CHANGE_LIKE="post:user:change:";//hash 后跟postId，fileduserId,value1or-1


    @Scheduled(fixedRate = 300000)
    public void syncLikeCountAndRelation(){
        //获取所有被修改的postId
        Set<String> postIdSet=stringRedisTemplate.opsForSet().members(POST_CHANGE_LIKE);
        if(postIdSet==null||postIdSet.isEmpty()) return;

        //同步点赞数
        for(String postId:postIdSet){
            String countKey=POST_LIKE_COUNT+postId;
            Long likeCount= Long.valueOf(stringRedisTemplate.opsForValue().get(countKey));
            //更新文章数据库like_count字段
            Post post = postMapper.getById(Long.parseLong(postId));
            post.setLikeCount(likeCount);
            postMapper.update(post);
        }

        //同步postlike关系
        //获取改变的用户并更新数据库
        for(String postId:postIdSet){
            String userChangeKey=USER_CHANGE_LIKE+postId;
            Map<Object, Object> changeUsers = stringRedisTemplate.opsForHash().entries(userChangeKey);
            Set<Object> userIds=changeUsers.keySet();
            for(Object userId:userIds){
                if(changeUsers.get(userId).equals("1")){
                    postLikeMapper.delete(Long.parseLong((String) userId), Long.parseLong(postId));
                    postLikeMapper.insert(Long.parseLong((String)userId),Long.parseLong(postId), LocalDateTime.now());
                }else{
                    postLikeMapper.delete(Long.parseLong((String) userId), Long.parseLong(postId));
                }
            }
        }

        //存放更改过的文章id
        for(String postId:postIdSet){
            stringRedisTemplate.opsForSet().add(POST_SCORE_CHANGE,postId);
        }

        //清空变更记录
        stringRedisTemplate.delete(POST_CHANGE_LIKE);
    }
}
