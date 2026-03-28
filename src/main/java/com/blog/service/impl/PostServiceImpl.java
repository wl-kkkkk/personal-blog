package com.blog.service.impl;

import com.blog.entity.Post;
import com.blog.mapper.PostMapper;
import com.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

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
        return postMapper.getById(id);
    }

    @Override
    public void add(Post post) {
        postMapper.insert(post);
    }

    @Override
    public void update(Post post) {
        postMapper.update(post);
    }

    @Override
    public void delete(Long id) {
        postMapper.delete(id);
    }

    @Override
    public List<Post> searchByTitle(String keyword){
        return postMapper.searchByTitle(keyword);
    }
}
