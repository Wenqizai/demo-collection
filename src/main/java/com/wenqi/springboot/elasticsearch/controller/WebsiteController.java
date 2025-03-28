package com.wenqi.springboot.elasticsearch.controller;

import com.wenqi.springboot.elasticsearch.model.Blog;
import com.wenqi.springboot.elasticsearch.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wenqi.springboot.elasticsearch.model.ResponseResult;

/**
 * 网站控制器，处理博客相关的REST请求
 */
@RestController
@RequestMapping("/website/blog")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    /**
     * 创建博客文档，如果文档已存在则创建失败
     */
    @PutMapping("/{id}/_create")
    public ResponseResult<String> createBlog(@PathVariable String id, @RequestBody Blog blog) {
        boolean success = websiteService.createBlog(id, blog);
        if (success) {
            return ResponseResult.success("Blog created successfully");
        } else {
            return ResponseResult.fail("Blog already exists");
        }
    }

    /**
     * 更新博客文档，如果文档不存在则创建
     */
    @PutMapping("/{id}")
    public ResponseResult<String> updateBlog(@PathVariable String id, @RequestBody Blog blog) {
        String documentId = websiteService.updateBlog(id, blog);
        return ResponseResult.success("Blog updated with ID: " + documentId);
    }
}