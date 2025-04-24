package com.wenqi.springboot.elasticsearch.service;

import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch服务接口
 */
public interface IElasticsearchService {
    /**
     * 创建索引
     */
    boolean createIndex(String index, String mappings);

    /**
     * 判断索引是否存在
     */
    boolean indexExists(String index);

    /**
     * 删除索引
     */
    boolean deleteIndex(String index);

    /**
     * 添加/更新文档
     */
    String addDocument(String index, String id, Object document);
    
    /**
     * 添加/更新文档(指定路由)
     */
    String addDocument(String index, String id, Object document, String routing);

    /**
     * 批量添加文档
     */
    boolean bulkAddDocument(String index, List<Object> documents);

    /**
     * 获取文档
     */
    Map<String, Object> getDocument(String index, String id);
    
    /**
     * 获取文档(指定路由)
     */
    Map<String, Object> getDocument(String index, String id, String routing);
    
    /**
     * 批量获取文档(支持路由)
     */
    List<Map<String, Object>> mgetDocuments(String index, List<String> ids, List<String> routings);

    /**
     * 更新文档
     */
    boolean updateDocument(String index, String id, Object document);
    
    /**
     * 更新文档(指定路由)
     */
    boolean updateDocument(String index, String id, Object document, String routing);

    /**
     * 删除文档
     */
    boolean deleteDocument(String index, String id);
    
    /**
     * 删除文档(指定路由)
     */
    boolean deleteDocument(String index, String id, String routing);

    /**
     * 搜索文档
     */
    <T> List<T> search(String index, QueryBuilder queryBuilder, Class<T> clazz);
    
    /**
     * 搜索文档(指定路由)
     */
    <T> List<T> search(String index, QueryBuilder queryBuilder, Class<T> clazz, String routing);
}