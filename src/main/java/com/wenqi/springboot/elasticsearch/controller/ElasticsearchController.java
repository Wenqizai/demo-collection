package com.wenqi.springboot.elasticsearch.controller;

import com.wenqi.springboot.elasticsearch.model.DocsRequest;
import com.wenqi.springboot.elasticsearch.service.IElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch控制器
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/es")
public class ElasticsearchController {

    private final IElasticsearchService elasticsearchService;

    /**
     * 创建索引
     */
    @PostMapping("/index/{index}")
    public boolean createIndex(@PathVariable String index, @RequestBody(required = false) String mappings) {
        return elasticsearchService.createIndex(index, mappings);
    }

    /**
     * 删除索引
     */
    @DeleteMapping("/index/{index}")
    public boolean deleteIndex(@PathVariable String index) {
        return elasticsearchService.deleteIndex(index);
    }

    /**
     * 判断索引是否存在
     */
    @GetMapping("/index/{index}")
    public boolean indexExists(@PathVariable String index) {
        return elasticsearchService.indexExists(index);
    }

    /**
     * 添加文档
     */
    @PostMapping("/{index}/document")
    public String addDocument(@PathVariable String index, @RequestParam(required = false) String id, @RequestBody Object document) {
        return elasticsearchService.addDocument(index, id, document);
    }

    /**
     * 获取文档
     */
    @GetMapping("/{index}/document/{id}")
    public Map<String, Object> getDocument(@PathVariable String index, @PathVariable String id) {
        return elasticsearchService.getDocument(index, id);
    }

    /**
     * 更新文档
     */
    @PutMapping("/{index}/document/{id}")
    public boolean updateDocument(@PathVariable String index, @PathVariable String id, @RequestBody Object document) {
        return elasticsearchService.updateDocument(index, id, document);
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{index}/document/{id}")
    public boolean deleteDocument(@PathVariable String index, @PathVariable String id) {
        return elasticsearchService.deleteDocument(index, id);
    }

    /**
     * 搜索文档
     */
    @GetMapping("/{index}/search")
    public <T> List<T> search(@PathVariable String index, @RequestParam String keyword, @RequestParam String field, @RequestParam Class<T> clazz) {
        return elasticsearchService.search(index, QueryBuilders.matchQuery(field, keyword), clazz);
    }

    /**
     * 搜索文档
     */
    @GetMapping("/{index}/phrase-search")
    public <T> List<T> phraseSearch(@PathVariable String index, @RequestParam String keyword, @RequestParam String field, @RequestParam Class<T> clazz) {
        return elasticsearchService.search(index, QueryBuilders.matchPhrasePrefixQuery(field, keyword), clazz);
    }

    /**
     * 添加/更新文档(指定路由)
     */
    @PutMapping("/{index}/{type}/{id}")
    public String addDocumentWithRouting(
            @PathVariable String index,
            @PathVariable String type,
            @PathVariable String id,
            @RequestParam(required = false) String routing,
            @RequestBody Object document) {
        if (routing != null && !routing.trim().isEmpty()) {
            return elasticsearchService.addDocument(index, id, document, routing);
        } else {
            return elasticsearchService.addDocument(index, id, document);
        }
    }

    /**
     * 获取文档(支持路由)
     */
    @GetMapping("/{index}/{type}/{id}")
    public Map<String, Object> getDocumentWithRouting(
            @PathVariable String index,
            @PathVariable String type,
            @PathVariable String id,
            @RequestParam(required = false) String routing) {
        if (routing != null && !routing.trim().isEmpty()) {
            return elasticsearchService.getDocument(index, id, routing);
        } else {
            return elasticsearchService.getDocument(index, id);
        }
    }

    /**
     * 批量获取文档(支持路由)
     */
    @GetMapping("/{index}/_mget")
    public List<Map<String, Object>> mgetDocuments(
            @PathVariable String index,
            @RequestBody(required = true) List<DocsRequest> docsRequests) {
        
        if (docsRequests == null || docsRequests.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> ids = docsRequests.stream()
                .map(DocsRequest::getId)
                .collect(Collectors.toList());
        
        List<String> routings = docsRequests.stream()
                .map(DocsRequest::getRouting)
                .map(routing -> (routing == null || routing.trim().isEmpty()) ? null : routing)
                .collect(Collectors.toList());
        
        return elasticsearchService.mgetDocuments(index, ids, routings);
    }

    /**
     * 搜索文档(支持路由)
     */
    @GetMapping("/{index}/{type}/_search")
    public <T> List<T> searchWithRouting(
            @PathVariable String index,
            @PathVariable String type,
            @RequestParam(required = false) String routing,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String field,
            @RequestParam Class<T> clazz) {
        
        if (routing != null && !routing.trim().isEmpty()) {
            return elasticsearchService.search(index, QueryBuilders.matchQuery(field, keyword), clazz, routing);
        } else {
            return elasticsearchService.search(index, QueryBuilders.matchQuery(field, keyword), clazz);
        }
    }

    /**
     * 搜索全部文档(不带参数)
     */
    @GetMapping("/{index}/{type}/_search_all")
    public <T> List<T> searchAll(
            @PathVariable String index,
            @PathVariable String type,
            @RequestParam Class<T> clazz) {
        return elasticsearchService.search(index, QueryBuilders.matchAllQuery(), clazz);
    }
}