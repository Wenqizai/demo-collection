package com.wenqi.springboot.elasticsearch.controller;

import com.wenqi.springboot.elasticsearch.service.IElasticsearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch控制器
 */
@RestController
@RequestMapping("/es")
public class ElasticsearchController {

    @Autowired
    private IElasticsearchService elasticsearchService;

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
}