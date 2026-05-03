package com.blog.service.impl;

import com.blog.dto.PostDetailDTO;
import com.blog.entity.Post;
import com.blog.service.PostCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostCountServiceImpl implements PostCountService {

    private static final String POST_LIKE_COUNT="post:like:count:";//值为String
    private static final String COMMENT_COUNT_KEY="comment:count:";//用string存储

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void fillPost(Post post) {
        //覆盖实时数据
        //获取key
        String likeCountKey=POST_LIKE_COUNT+post.getId();
        String commentCountKey=COMMENT_COUNT_KEY+post.getId();

        //获取数据，看是否是空的
        String likeCountStr=stringRedisTemplate.opsForValue().get(likeCountKey);
        String commentCountStr=stringRedisTemplate.opsForValue().get(commentCountKey);

        //覆盖
        if(likeCountStr!=null){
            post.setLikeCount(Long.parseLong(likeCountStr));
        }
        if(commentCountStr!=null){
            post.setCommentCount(Long.parseLong(commentCountStr));
        }
    }

    @Override
    public void fillPost(PostDetailDTO post) {
        //覆盖实时数据
        //获取key
        String likeCountKey=POST_LIKE_COUNT+post.getId();
        String commentCountKey=COMMENT_COUNT_KEY+post.getId();

        //获取数据，看是否是空的
        String likeCountStr=stringRedisTemplate.opsForValue().get(likeCountKey);
        String commentCountStr=stringRedisTemplate.opsForValue().get(commentCountKey);

        //覆盖
        if(likeCountStr!=null){
            post.setLikeCount(Long.parseLong(likeCountStr));
        }
        if(commentCountStr!=null){
            post.setCommentCount(Long.parseLong(commentCountStr));
        }
    }

    @Override
    public void fillPosts(List<Post> posts) {
        for(Post post:posts){

            //获取key
            String likeCountKey=POST_LIKE_COUNT+post.getId();
            String commentCountKey=COMMENT_COUNT_KEY+post.getId();

            //获取数据，看是否是空的
            String likeCountStr=stringRedisTemplate.opsForValue().get(likeCountKey);
            String commentCountStr=stringRedisTemplate.opsForValue().get(commentCountKey);

            //覆盖
            if(likeCountStr!=null){
                post.setLikeCount(Long.parseLong(likeCountStr));
            }
            if(commentCountStr!=null){
                post.setCommentCount(Long.parseLong(commentCountStr));
            }
        }
    }
}
