package com.blog.service.impl;

import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import com.blog.service.PostService;
import com.blog.utils.CacheClient;
import com.blog.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    private static final String HOT_POST_KEY="hot:posts";
    private static final String POST_VIEW_KEY="posts:view:";
    private static final String GET_POST_KEY="cache:post:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CacheClient cacheClient;

    @Override
    public List<Post> list() {
        return postMapper.findAll();
    }

    @Override
    public List<Post> page(int pageNum,int pageSize){
        int offset = (pageNum-1)*pageSize;
        return postMapper.getByPage(offset,pageSize);
    }

    @Override
    public Post getById(Long id) {
        //避免缓存穿透+缓存雪崩
        Post post = cacheClient.getWithPassThrough(GET_POST_KEY, id, Post.class, postMapper::getById);
        return post;
        /*//避免缓存穿透
        String strJson = stringRedisTemplate.opsForValue().get(GET_POST_KEY + id);
        //查询缓存
        if(StringUtils.isNotBlank(strJson)){
            try {
                Post post = objectMapper.readValue(strJson,Post.class);
                return post;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("缓存反序列化失败");
            }
        }
        //是否是空内容,命中的是""
        if(strJson!=null){
            return null;
        }
        //查询数据库
        Post post =postMapper.getById(id);
        //数据库没有，缓存空对象
        if(post==null){
            try {
                stringRedisTemplate.opsForValue().set(GET_POST_KEY+id,objectMapper.writeValueAsString(""));
                return null;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("写入空值失败");
            }
        }
        //防止缓存雪崩并写入redis
        try {
            long ttl=10+ RandomUtils.nextInt(0,100);
            stringRedisTemplate.opsForValue().set(GET_POST_KEY+id, objectMapper.writeValueAsString(post),ttl,TimeUnit.MINUTES);
            return post;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("缓存写入失败");
        }*/
    }

    @Override
    public void add(Post post) {
        postMapper.insert(post);
        //第一次查询写入缓存即可
    }

    @Override
    public void update(Post post) {
        postMapper.update(post);
        //更多删少
        Long id=post.getId();
        stringRedisTemplate.delete(GET_POST_KEY+id);
        //延迟双删
        new Thread(()->{
            try {
                Thread.sleep(100);
                stringRedisTemplate.delete(GET_POST_KEY+id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void delete(Long id) {
        postMapper.delete(id);
        stringRedisTemplate.delete(GET_POST_KEY+id);
        //延迟双删
        new Thread(()->{
            try {
                Thread.sleep(100);
                stringRedisTemplate.delete(GET_POST_KEY+id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public List<Post> searchByTitle(String keyword){
        return postMapper.searchByTitle(keyword);
    }

    @Override
    public List<Post> getHotPosts(){

        List<Post> hotPosts=cacheClient
                .getWithLogicalExpire(HOT_POST_KEY,postMapper::selectHotPosts);

        return hotPosts;
        /*String strJson = stringRedisTemplate.opsForValue().get(HOT_POST_KEY);
        if(strJson==null){
            return null;
        }

        //命中，看是否过期
        RedisData hotPosts = null;
        try {
            hotPosts = objectMapper.readValue(strJson, RedisData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("未能写入数据");
        }
        LocalDateTime expireTime=hotPosts.getExpireTime();

        //未过期，返回数据即可
        if(expireTime.isAfter(LocalDateTime.now())){
            return (List<Post>) hotPosts.getData();
        }

        //过期，缓存重建
        Boolean isLock=tryLock(GET_LOCK_KEY);
        //成功获取互斥锁
        if(!isLock){
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    List<Post> newhotPosts=postMapper.selectHotPosts();
                    RedisData reHotPosts=new RedisData(newhotPosts,LocalDateTime.now().plusHours(24L));
                    stringRedisTemplate.opsForValue().set(HOT_POST_KEY,objectMapper
                            .writeValueAsString(reHotPosts),24L,TimeUnit.HOURS);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                } finally {
                    unLock(GET_LOCK_KEY);
                }
            });
        }
        //未能获取互斥锁
        return (List<Post>) hotPosts.getData();*/
    }


/*    @Override
    public void likeBlog(Long id){
        //获取登录用户
        Long userId = UserContext.getUserId();

        //判断当前登录用户是否点赞
        String key="blog:liked:"+id;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        if(BooleanUtils.isFalse(isMember)){
            boolean isSuccess=postMapper.incrLikeCount(id);
            if(isSuccess){
                stringRedisTemplate.opsForSet().add(key,userId.toString());
            }
        }else{
            boolean isSuccess=postMapper.decrLikeCount(id);
            if(isSuccess){
                stringRedisTemplate.opsForSet().remove(key,userId.toString());
            }
        }
    }*/


}
