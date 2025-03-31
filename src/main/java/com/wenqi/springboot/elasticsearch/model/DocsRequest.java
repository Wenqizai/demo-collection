package com.wenqi.springboot.elasticsearch.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * 批量获取文档的请求参数
 */
@Builder
@Data
public class DocsRequest {
    /**
     * 索引名称
     */
    private String index;

    /**
     * 文档ID
     */
    private String id;

    /**
     * 需要获取的字段集合
     */
    private Set<String> fields;
}