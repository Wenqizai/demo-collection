package com.wenqi.springboot.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.wenqi.springboot.elasticsearch.exception.BusinessException;
import com.wenqi.springboot.elasticsearch.model.Blog;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 网站服务类，处理博客相关操作
 */
@Service
public class WebsiteService {

    @Autowired
    private RestHighLevelClient client;

    private static final String INDEX = "website";
    private static final String TYPE = "blog";

    /**
     * 创建博客文档，如果文档已存在则创建失败
     *
     * @param id   文档ID
     * @param blog 博客内容
     * @return 是否创建成功
     */
    public boolean createBlog(String id, Blog blog) {
        IndexRequest request = new IndexRequest(INDEX)
                .id(id)
                .opType("create")
                .source(JSON.toJSONString(blog), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("version conflict")) {
                return false;
            }
            throw new BusinessException("创建博客文档失败", e);
        }
    }

    /**
     * 更新博客文档，如果文档不存在则创建
     *
     * @param id   文档ID
     * @param blog 博客内容
     * @return 文档ID
     */
    public String updateBlog(String id, Blog blog) {
        IndexRequest request = new IndexRequest(INDEX)
                .id(id)
                .source(JSON.toJSONString(blog), XContentType.JSON);
        try {
            return client.index(request, RequestOptions.DEFAULT).getId();
        } catch (IOException e) {
            throw new BusinessException("更新博客文档失败", e);
        }
    }
}