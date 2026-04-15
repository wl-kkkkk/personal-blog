package com.blog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class CacheClient {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String GET_LOCK_KEY="cache:lock";

    //创建线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);


    /*
    *需要获取的key
    *需要获取的id
    *需要获取的类型
    *降级查找的方法（一参数有返回值）
    * */

    public <R> R getWithPassThrough(String key, Long id, Class<R> type, Function<Long, R> dbFallback){

        //避免缓存穿透
        String strJson = stringRedisTemplate.opsForValue().get(key + id);
        //查询缓存
        if(StringUtils.isNotBlank(strJson)){
            try {
                //泛型擦除，需要传进来类型
                R r = objectMapper.readValue(strJson,type);
                return r;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("缓存反序列化失败");
            }
        }
        //是否是空内容,命中的是""
        if(strJson!=null){
            return null;
        }
        //查询数据库
        R r = dbFallback.apply(id);
        //数据库没有，缓存空对象
        if(r==null){
            try {
                stringRedisTemplate.opsForValue().set(key+id,objectMapper.writeValueAsString(""));
                return null;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("写入空值失败");
            }
        }
        //防止缓存雪崩并写入redis
        try {
            long ttl=10+ RandomUtils.nextInt(0,100);
            stringRedisTemplate.opsForValue().set(key+id, objectMapper.writeValueAsString(r),ttl, TimeUnit.MINUTES);
            return r;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("缓存写入失败");
        }
    }

    public boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue()
                .setIfAbsent(key,"1",10L,TimeUnit.SECONDS);
        return flag;
    }
    public void unLock(String key){
        stringRedisTemplate.delete(key);
    }

    /*
    *需要获取的key
    *降级查找的方法（无参数有返回值）
    * */

    public <R> R getWithLogicalExpire(String key,Supplier<R> dbFallback){
        String strJson = stringRedisTemplate.opsForValue().get(key);
        if(strJson==null){
            return null;
        }

        //命中，看是否过期
        RedisData r = null;
        try {
            r = objectMapper.readValue(strJson, RedisData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("未能写入数据");
        }
        LocalDateTime expireTime=r.getExpireTime();

        //未过期，返回数据即可
        if(expireTime.isAfter(LocalDateTime.now())){
            return (R) r.getData();
        }

        //过期，缓存重建
        Boolean isLock=tryLock(GET_LOCK_KEY);
        //成功获取互斥锁
        if(isLock){
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    R newr=dbFallback.get();
                    RedisData reHotPosts=new RedisData(newr,LocalDateTime.now().plusHours(24L));
                    stringRedisTemplate.opsForValue().set(key,objectMapper
                            .writeValueAsString(reHotPosts),24L,TimeUnit.HOURS);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                } finally {
                    unLock(GET_LOCK_KEY);
                }
            });
        }
        //未能获取互斥锁
        return (R) r.getData();

    }
}
