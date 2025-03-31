package com.wenqi.springboot.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.wenqi.springboot.elasticsearch.exception.BusinessException;
import com.wenqi.springboot.elasticsearch.model.Blog;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        } catch (Exception e) {
            throw new BusinessException("更新博客文档失败", e);
        }
    }

    /**
     * 获取博客文档，支持三种不同的获取方式：
     * 1. 获取完整文档 (?pretty)
     * 2. 获取指定字段 (?_source=field1,field2)
     * 3. 仅获取source内容
     *
     * @param id           文档ID
     * @param sourceFields 需要获取的字段，如果为null则获取全部字段
     * @param sourceOnly   是否仅获取source内容
     * @return 博客文档内容
     */
    public Map<String, Object> getBlog(String id, Set<String> sourceFields, boolean sourceOnly) {
        GetRequest request = new GetRequest(INDEX, id);

        // 如果指定了字段，则只获取指定字段
        if (sourceFields != null && !sourceFields.isEmpty()) {
            request.fetchSourceContext(new org.elasticsearch.search.fetch.subphase.FetchSourceContext(
                    true,
                    sourceFields.toArray(new String[0]),
                    null
            ));
        }

        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);

            // 如果文档不存在，返回null
            if (!response.isExists()) {
                return null;
            }

            // 如果只需要source内容，直接返回source
            if (sourceOnly) {
                return response.getSourceAsMap();
            }

            // 返回完整文档，包括元数据
            Map<String, Object> result = response.getSourceAsMap();
            result.put("_index", response.getIndex());
            result.put("_type", response.getType());
            result.put("_id", response.getId());
            result.put("_version", response.getVersion());
            result.put("found", response.isExists());
            return result;
        } catch (Exception e) {
            throw new BusinessException("获取博客文档失败", e);
        }
    }

    /**
     * 根据视图数量条件删除博客文档
     *
     * @param id    文档ID
     * @param count 视图数量条件
     * @return 是否执行了删除操作
     */
    public boolean deleteBlogByViewCount(String id, int count) {
        try {
            UpdateRequest request = new UpdateRequest(INDEX, id);

            // 创建条件删除脚本
            String scriptContent = "ctx.op = ctx._source.views == params.count ? 'delete' : 'none'";
            Map<String, Object> params = Collections.singletonMap("count", count);

            Script script = new Script(
                    ScriptType.INLINE,
                    "painless",
                    scriptContent,
                    params
            );

            request.script(script);

            // 执行更新请求
            client.update(request, RequestOptions.DEFAULT);

            // 如果文档不存在, 则会抛异常
            return true;
        } catch (Exception e) {
            if (e.getMessage().contains("document_missing_exception")) {
                return false;
            }
            throw new BusinessException("删除博客文档失败", e);
        }
    }

    /**
     * 通用脚本更新方法
     *
     * @param id             文档ID
     * @param scriptContent  脚本内容
     * @param params         脚本参数
     * @return 更新结果，包含更新状态和版本信息
     */
    public Map<String, Object> updateByScript(String id, String scriptContent, Map<String, Object> params) {
        try {
            UpdateRequest request = new UpdateRequest(INDEX, id);

            Script script = new Script(
                    ScriptType.INLINE,
                    "painless",
                    scriptContent,
                    params
            );

            request.script(script);

            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            Map<String, Object> result = response.getGetResult().sourceAsMap();
            result.put("_version", response.getVersion());
            result.put("result", response.getResult().name());
            return result;
        } catch (Exception e) {
            if (e.getMessage().contains("document_missing_exception")) {
                throw new BusinessException("文档不存在", e);
            }
            throw new BusinessException("脚本更新文档失败", e);
        }
    }
}