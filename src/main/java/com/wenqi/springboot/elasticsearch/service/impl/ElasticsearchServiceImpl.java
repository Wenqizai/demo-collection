package com.wenqi.springboot.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.wenqi.springboot.elasticsearch.exception.BusinessException;
import com.wenqi.springboot.elasticsearch.service.IElasticsearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch服务实现类
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ElasticsearchServiceImpl implements IElasticsearchService {

    private final RestHighLevelClient client;

    /**
     * 创建索引
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
     * 添加/更新文档(指定路由)
     */
    @Override
    public String addDocument(String index, String id, Object document, String routing) {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        if (routing != null && !routing.trim().isEmpty()) {
            request.routing(routing);
        }
        request.source(JSON.toJSONString(document), XContentType.JSON);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            return response.getId();
        } catch (Exception e) {
            log.error("添加文档失败, index: {}, id: {}, routing: {}, document: {}", index, id, routing, document, e);
            throw new BusinessException("添加文档失败", e);
        }
    }

    /**
     * 批量添加文档
     */
    @Override
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
    @Override
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
     * 获取文档(指定路由)
     */
    @Override
    public Map<String, Object> getDocument(String index, String id, String routing) {
        GetRequest request = new GetRequest(index, id);
        if (routing != null && !routing.trim().isEmpty()) {
            request.routing(routing);
        }
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.getSource();
        } catch (Exception e) {
            log.error("获取文档失败, index: {}, id: {}, routing: {}", index, id, routing, e);
            throw new BusinessException("获取文档失败", e);
        }
    }
    
    /**
     * 批量获取文档(支持路由)
     */
    @Override
    public List<Map<String, Object>> mgetDocuments(String index, List<String> ids, List<String> routings) {
        MultiGetRequest request = new MultiGetRequest();
        
        for (int i = 0; i < ids.size(); i++) {
            MultiGetRequest.Item item = new MultiGetRequest.Item(index, ids.get(i));
            if (routings != null && routings.size() > i && routings.get(i) != null) {
                item.routing(routings.get(i));
            }
            request.add(item);
        }
        
        try {
            MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (MultiGetItemResponse item : response.getResponses()) {
                if (item.getResponse() != null && item.getResponse().isExists()) {
                    result.add(item.getResponse().getSourceAsMap());
                } else {
                    // 如果文档不存在，添加一个空映射
                    result.add(new HashMap<>());
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("批量获取文档失败, index: {}, ids: {}, routings: {}", index, ids, routings, e);
            throw new BusinessException("批量获取文档失败", e);
        }
    }

    /**
     * 更新文档
     */
    @Override
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
     * 更新文档(指定路由)
     */
    @Override
    public boolean updateDocument(String index, String id, Object document, String routing) {
        UpdateRequest request = new UpdateRequest(index, id);
        if (routing != null && !routing.trim().isEmpty()) {
            request.routing(routing);
        }
        request.doc(JSON.toJSONString(document), XContentType.JSON);
        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            return response.getResult().name().equals("UPDATED");
        } catch (Exception e) {
            log.error("更新文档失败, index: {}, id: {}, routing: {}, document: {}", index, id, routing, document, e);
            throw new BusinessException("更新文档失败", e);
        }
    }

    /**
     * 删除文档
     */
    @Override
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
     * 删除文档(指定路由)
     */
    @Override
    public boolean deleteDocument(String index, String id, String routing) {
        DeleteRequest request = new DeleteRequest(index, id);
        if (routing != null && !routing.trim().isEmpty()) {
            request.routing(routing);
        }
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            return response.getResult().name().equals("DELETED");
        } catch (Exception e) {
            log.error("删除文档失败, index: {}, id: {}, routing: {}", index, id, routing, e);
            throw new BusinessException("删除文档失败", e);
        }
    }

    /**
     * 搜索文档
     */
    @Override
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

    /**
     * 搜索文档(指定路由)
     */
    @Override
    public <T> List<T> search(String index, QueryBuilder queryBuilder, Class<T> clazz, String routing) {
        SearchRequest request = new SearchRequest(index);
        if (routing != null && !routing.trim().isEmpty()) {
            request.routing(routing);
        }
        
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
            log.error("搜索文档失败, index: {}, routing: {}, queryBuilder: {}, clazz: {}", 
                     index, routing, queryBuilder, clazz, e);
            throw new BusinessException("搜索文档失败", e);
        }
    }
}