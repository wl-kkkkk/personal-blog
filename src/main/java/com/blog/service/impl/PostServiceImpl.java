package com.blog.service.impl;

import com.blog.dto.PostDetailDTO;
import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import com.blog.service.PostService;
import com.blog.utils.CacheClient;
import com.blog.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    private static final String HOT_POST_KEY="posts:hot:zset";
    private static final String GET_POST_KEY="cache:post:";
    private static final String GET_DETAIL_KEY="cache:detail:";
    private static final String GET_POSTS_KEY="cache:posts:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private PostCountServiceImpl postCountService;

    @Override
    public List<Post> list() {
        return postMapper.findAll();
    }

    @Override
    public List<Post> page(int pageNum,int pageSize){
        int offset = (pageNum-1)*pageSize;
        List<Post> postsbyPage = postMapper.getByPage(offset, pageSize);
        postCountService.fillPosts(postsbyPage);
        return postsbyPage;
    }

    /*
     * 通过id获取文章详细内容
     * controller层：getById
     * service层：Post getById
     * mapper:getById
     * */
    @Override
    public Post getById(Long id) {

        //避免缓存穿透+缓存雪崩
        Post post = cacheClient.getWithPassThrough(GET_POST_KEY, id, Post.class, postMapper::getById);

        if(post==null){
            return post;
        }

        //覆盖实时数据
        postCountService.fillPost(post);

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
    public PostDetailDTO getPostDetailById(Long postId){

        //避免缓存穿透+缓存雪崩
        PostDetailDTO post = cacheClient.getWithPassThrough(GET_DETAIL_KEY,postId,PostDetailDTO.class,postMapper::getPostDetailById);

        if(post==null){
            return post;
        }

        //覆盖实时数据
        postCountService.fillPost(post);

        return post;
    }

    /*
     * 获取某个用户的所有文章
     * controller层：getPostsByUserId
     * service层：searchByUserId
     * map层：searchByUserId
     * */
    @Override
    public List<Post> searchByUserId(Long userId){
        List<Object> postIds = cacheClient.getWithPassThrough(GET_POSTS_KEY, userId, List.class, postMapper::searchByUserId);
        if(postIds==null){
            return null;
        }
        List<Post> posts = postIds.stream()
                .map(id -> ((Number) id).longValue())
                .map(postMapper::getById)
                .collect(Collectors.toList());
        //覆盖实时数据
        postCountService.fillPosts(posts);
        return posts;
    }


    @Override
    public void add(Post post) {
        Long userId= UserContext.getUserId();
        post.setUserId(userId);
        post.setCreateTime(new Date());
        postMapper.insert(post);

        String key=GET_POSTS_KEY+userId;
        stringRedisTemplate.delete(key);
        //不能一次查询写入缓存即可,懒加载//访问文章的时候就访问不到了//错了//哈哈最后还是懒加载
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
        List<Post> posts = postMapper.searchByTitle(keyword);

        if(posts==null||posts.isEmpty()){
            return null;
        }

        //覆盖实时数据
        postCountService.fillPosts(posts);

        return posts;
    }


    /*@Override
    public List<Post> getHotPosts(){

        List<Post> hotPosts=cacheClient
                .getWithLogicalExpire(HOT_POST_KEY,postMapper::selectHotPosts);

        return hotPosts;
        String strJson = stringRedisTemplate.opsForValue().get(HOT_POST_KEY);
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
        return (List<Post>) hotPosts.getData();*//*
    }*/
    /*
     * 获取热榜
     * controller层：getHotPosts
     * service层：getHotPosts
     * mapper层：selectPostsById(postIds)
     * */
    @Override
    public List<Post> getHotPosts(){
        //获取前十的post
        Set<String> postIds = stringRedisTemplate.opsForZSet().reverseRange(HOT_POST_KEY, 0, 9);

        if(postIds.isEmpty()){
            return new ArrayList<>();
        }

        //查找文章
        List<Post> Posts=postMapper.selectPostsByIds(postIds);

        //排序
        Map<String,Post> postMap=Posts
                .stream()
                .collect(Collectors.toMap(p->p.getId().toString(),p->p));
        List<Post> hotPosts=new ArrayList<>();
        for(String postId:postIds){
            hotPosts.add(postMap.get(postId));
        }

        //覆盖新数据
        postCountService.fillPosts(hotPosts);

        return hotPosts;
    }

}
