package com.blog.service.impl;

import com.blog.entity.Comment;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.PostMapper;
import com.blog.service.CommentService;
import com.blog.utils.CacheClient;
import com.blog.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CacheClient cacheClient;

    private static final String COMMENT_COUNT_KEY="comment:count:";//用string存储
    private static final String COMMENT_CHANGED_KEY="comment:changed";//用set存储更新过评论数量的文章id
    private static final String HOT_POST_KEY="posts:hot:zset";
    private static final String USER_COMMENT_KEY="user:comment:";

    @Override
    public void publishComment(Comment comment) {

        Long userId= UserContext.getUserId();
        comment.setUserId(userId);
        if(comment.getParentId()==null) comment.setParentId(0L);
        commentMapper.publishComment(comment);

        Long postId=comment.getPostId();
        //定时任务更新文章评论数
        String commentCountKey=COMMENT_COUNT_KEY+postId;
        //先做个懒加载，看是否在内存中
        Boolean isExist = stringRedisTemplate.hasKey(commentCountKey);
        if(isExist==false){
            stringRedisTemplate.opsForValue().set(commentCountKey,postMapper.getById(postId).getCommentCount().toString());
        }
        stringRedisTemplate.opsForValue().increment(commentCountKey);

        //实时更新热榜+最终一致性
        stringRedisTemplate.opsForZSet().incrementScore(HOT_POST_KEY,postId.toString(),3);
        //记录改变的文章
        stringRedisTemplate.opsForSet().add(COMMENT_CHANGED_KEY,postId.toString());
    }

    @Override
    public List<Comment> selectByPostId(Long postId) {
        return commentMapper.selectByPostId(postId);
    }

    @Override
    public List<Comment> listByPostId(Long postId){
        //我决定用hashmap放所有评论去查找而不是一个一个找
        Map<Long,Comment> hashmap=new HashMap<>();
        List<Comment> allComments=commentMapper.selectByPostId(postId);
        for(Comment comment:allComments){
            hashmap.put(comment.getId(),comment);
        }

        //根评论
        List<Comment> roots=new ArrayList<>();

        //实现,类似邻接表
        for(Comment comment:allComments){
            if(comment.getParentId()==null||comment.getParentId()==0) roots.add(comment);
            else{
                Comment parent=hashmap.get(comment.getParentId());
                parent.getChildren().add(comment);
            }
        }
        return roots;
    }

    @Override
    public List<Comment> selectByUserId(Long userId){
        List<Comment> comments = cacheClient.getWithPassThrough(USER_COMMENT_KEY, userId, List.class, commentMapper::selectByUserId);
        return comments;
    }


}
