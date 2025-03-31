package com.wenqi.springboot.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.wenqi.springboot.elasticsearch.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch服务实现类
 */
@Service
@Slf4j
public class ElasticsearchServiceImpl implements IElasticsearchService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 创建索引
     */
    public boolean createIndex(String index, String mappings) {
        CreateIndexRequest request = new CreateIndexRequest(index);
        if (mappings != null && !mappings.isEmpty()) {
            request.mapping(mappings, XContentType.JSON);
        }
        try {
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            log.error("创建索引失败, index: {}, mappings: {}", index, mappings, e);
            throw new BusinessException("创建索引失败", e);
        }
    }

    /**
     * 判断索引是否存在
     */
    public boolean indexExists(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("检查索引是否存在失败, index: {}", index, e);
            throw new BusinessException("检查索引是否存在失败", e);
        }
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            log.error("删除索引失败, index: {}", index, e);
            throw new BusinessException("删除索引失败", e);
        }
    }

    /**
     * 添加/更新文档
     */
    public String addDocument(String index, String id, Object document) {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        request.source(JSON.toJSONString(document), XContentType.JSON);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response.getId();
        } catch (Exception e) {
            log.error("添加文档失败, index: {}, id: {}, document: {}", index, id, document, e);
            throw new BusinessException("添加文档失败", e);
        }
    }

    /**
     * 批量添加文档
     */
    public boolean bulkAddDocument(String index, List<Object> documents) {
        BulkRequest request = new BulkRequest();
        for (Object document : documents) {
            request.add(new IndexRequest(index).source(JSON.toJSONString(document), XContentType.JSON));
        }
        try {
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            return !response.hasFailures();
        } catch (Exception e) {
            log.error("批量添加文档失败, index: {}, documents: {}", index, documents, e);
            throw new BusinessException("批量添加文档失败", e);
        }
    }

    /**
     * 获取文档
     */
    public Map<String, Object> getDocument(String index, String id) {
        GetRequest request = new GetRequest(index, id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.getSource();
        } catch (Exception e) {
            log.error("获取文档失败, index: {}, id: {}", index, id, e);
            throw new BusinessException("获取文档失败", e);
        }
    }

    /**
     * 更新文档
     */
    public boolean updateDocument(String index, String id, Object document) {
        UpdateRequest request = new UpdateRequest(index, id);
        request.doc(JSON.toJSONString(document), XContentType.JSON);
        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            return response.getResult().name().equals("UPDATED");
        } catch (Exception e) {
            log.error("更新文档失败, index: {}, id: {}, document: {}", index, id, document, e);
            throw new BusinessException("更新文档失败", e);
        }
    }

    /**
     * 删除文档
     */
    public boolean deleteDocument(String index, String id) {
        DeleteRequest request = new DeleteRequest(index, id);
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            return response.getResult().name().equals("DELETED");
        } catch (Exception e) {
            log.error("删除文档失败, index: {}, id: {}", index, id, e);
            throw new BusinessException("删除文档失败", e);
        }
    }

    /**
     * 搜索文档
     */
    public <T> List<T> search(String index, QueryBuilder queryBuilder, Class<T> clazz) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        request.source(sourceBuilder);

        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<T> result = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                result.add(JSON.parseObject(hit.getSourceAsString(), clazz));
            }
            return result;
        } catch (Exception e) {
            log.error("搜索文档失败, index: {}, queryBuilder: {}, clazz: {}", index, queryBuilder, clazz, e);
            throw new BusinessException("搜索文档失败", e);
        }
    }
}