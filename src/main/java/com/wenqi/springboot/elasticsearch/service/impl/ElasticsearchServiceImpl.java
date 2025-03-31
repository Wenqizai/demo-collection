package com.wenqi.springboot.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.wenqi.springboot.elasticsearch.exception.BusinessException;
import com.wenqi.springboot.elasticsearch.service.IElasticsearchService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchServiceImpl implements IElasticsearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean createIndex(String index, String mappings) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.source(mappings, XContentType.JSON);
            return client.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
        } catch (Exception e) {
            throw new BusinessException("创建索引失败", e);
        }
    }

    @Override
    public boolean indexExists(String index) {
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new BusinessException("检查索引是否存在失败", e);
        }
    }

    @Override
    public boolean deleteIndex(String index) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            throw new BusinessException("删除索引失败", e);
        }
    }

    @Override
    public String addDocument(String index, String id, Object document) {
        try {
            IndexRequest request = new IndexRequest(index)
                    .id(id)
                    .source(JSON.toJSONString(document), XContentType.JSON);
            return client.index(request, RequestOptions.DEFAULT).getId();
        } catch (Exception e) {
            throw new BusinessException("添加文档失败", e);
        }
    }

    @Override
    public boolean bulkAddDocument(String index, List<Object> documents) {
        try {
            BulkRequest request = new BulkRequest();
            for (Object document : documents) {
                request.add(new IndexRequest(index)
                        .source(JSON.toJSONString(document), XContentType.JSON));
            }
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            return !response.hasFailures();
        } catch (Exception e) {
            throw new BusinessException("批量添加文档失败", e);
        }
    }

    @Override
    public Map<String, Object> getDocument(String index, String id) {
        try {
            GetRequest request = new GetRequest(index, id);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (Exception e) {
            throw new BusinessException("获取文档失败", e);
        }
    }

    @Override
    public boolean updateDocument(String index, String id, Object document) {
        try {
            UpdateRequest request = new UpdateRequest(index, id)
                    .doc(JSON.toJSONString(document), XContentType.JSON);
            return client.update(request, RequestOptions.DEFAULT).getResult().name().equals("UPDATED");
        } catch (Exception e) {
            throw new BusinessException("更新文档失败", e);
        }
    }

    @Override
    public boolean deleteDocument(String index, String id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            return client.delete(request, RequestOptions.DEFAULT).getResult().name().equals("DELETED");
        } catch (Exception e) {
            throw new BusinessException("删除文档失败", e);
        }
    }

    @Override
    public <T> List<T> search(String index, QueryBuilder queryBuilder, Class<T> clazz) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(queryBuilder);
            org.elasticsearch.action.search.SearchRequest request = new org.elasticsearch.action.search.SearchRequest(index);
            request.source(sourceBuilder);
            
            org.elasticsearch.action.search.SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<T> result = new ArrayList<>();
            response.getHits().forEach(hit -> result.add(JSON.parseObject(hit.getSourceAsString(), clazz)));
            return result;
        } catch (Exception e) {
            throw new BusinessException("搜索文档失败", e);
        }
    }
}