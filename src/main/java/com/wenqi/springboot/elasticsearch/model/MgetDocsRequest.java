package com.wenqi.springboot.elasticsearch.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 批量获取文档的请求参数
 */
@Data
public class MgetDocsRequest {
    /**
     * 索引列表
     */
    private List<String> indices;

    /**
     * 文档ID列表
     */
    private List<String> ids;

    /**
     * 需要获取的字段列表
     */
    private List<Set<String>> fields;
    
    /**
     * 路由值列表，与文档ID一一对应
     */
    private List<String> routings;
}