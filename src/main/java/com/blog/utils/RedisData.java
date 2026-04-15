package com.blog.utils;

import com.blog.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

public class RedisData {
    private LocalDateTime expireTime;
    private Object data;

    public RedisData(){}

    public RedisData( Object data,LocalDateTime expireTime) {
        this.expireTime = expireTime;
        this.data = data;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
