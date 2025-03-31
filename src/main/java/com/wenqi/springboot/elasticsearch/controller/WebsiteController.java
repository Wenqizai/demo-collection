package com.wenqi.springboot.elasticsearch.controller;

import com.wenqi.springboot.elasticsearch.model.Blog;
import com.wenqi.springboot.elasticsearch.model.DocsRequest;
import com.wenqi.springboot.elasticsearch.model.ResponseResult;
import com.wenqi.springboot.elasticsearch.model.ScriptUpdateRequest;
import com.wenqi.springboot.elasticsearch.service.IWebsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 网站控制器，处理博客相关的REST请求
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/website/blog")
public class WebsiteController {

    private final IWebsiteService websiteService;

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

    /**
     * 根据视图数量条件删除博客文档
     */
    @PostMapping("/{id}/_update")
    public ResponseResult<String> deleteBlogByViewCount(@PathVariable String id, @RequestParam int count) {
        boolean deleted = websiteService.deleteBlogByViewCount(id, count);
        if (deleted) {
            return ResponseResult.success("Blog deleted successfully");
        } else {
            return ResponseResult.fail("Failed to delete blog: view count does not match");
        }
    }

    /**
     * 通过脚本更新博客文档
     */
    @PostMapping("/{id}/_update_by_script")
    public ResponseResult<Map<String, Object>> updateBlogByScript(
            @PathVariable String id,
            @RequestBody ScriptUpdateRequest request) {
        Map<String, Object> result = websiteService.updateByScript(id, request.getScript(), request.getParams());
        return ResponseResult.success(result);
    }

    /**
     * 获取博客文档，支持三种不同的获取方式：
     * 1. 获取完整文档 (?pretty)
     * 2. 获取指定字段 (?fields=field1,field2)
     * 3. 仅获取source内容 (?sourceOnly=true)
     */
    @GetMapping("/{id}")
    public ResponseResult<Map<String, Object>> getBlog(
            @PathVariable String id,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false, defaultValue = "false") boolean sourceOnly) {
        Set<String> sourceFields = null;
        if (fields != null && !fields.isEmpty()) {
            sourceFields = new HashSet<>(Arrays.asList(fields.split(",")));
        }
        Map<String, Object> blog = websiteService.getBlog(id, sourceFields, sourceOnly);
        if (blog == null) {
            return ResponseResult.fail("Blog not found");
        }
        return ResponseResult.success(blog);
    }

    /**
     * 批量获取多个文档
     *
     * @param requests 批量请求参数列表，每个请求包含索引名称、文档ID和需要获取的字段
     * @return 文档列表
     */
    @PostMapping("/mget/docs")
    public ResponseResult<List<Map<String, Object>>> mgetByDocs(@RequestBody List<DocsRequest> requests) {
        return ResponseResult.success(websiteService.mgetByDocs(requests));
    }

    /**
     * 批量获取指定索引的文档
     *
     * @param index  索引名称
     * @return 文档列表
     */
    @PostMapping("/mget/{index}")
    public ResponseResult<List<Map<String, Object>>> mgetByIndexAndIds(
            @PathVariable String index,
            @RequestBody(required = false) List<DocsRequest> requests) {
        if (!CollectionUtils.isEmpty(requests)) {
            requests.forEach(request -> request.setIndex(index));
        }
        return ResponseResult.success(websiteService.mgetByDocs(requests));
    }

    /**
     * 批量获取默认索引的文档
     *
     * @param ids 文档ID列表
     * @return 文档列表
     */
    @GetMapping("/mget")
    public ResponseResult<List<Map<String, Object>>> mgetByIds(@RequestBody List<String> ids) {
        return ResponseResult.success(websiteService.mgetByIds(ids));
    }
}