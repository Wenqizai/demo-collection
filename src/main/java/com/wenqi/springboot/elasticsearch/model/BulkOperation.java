package com.wenqi.springboot.elasticsearch.model;

import lombok.Data;

import java.util.Map;

/**
 * 批量操作请求模型
 */
@Data
public class BulkOperation {
    /**
     * 操作类型：create, update, delete, index
     */
    private String operation;

    /**
     * 文档ID
     */
    private String id;

    /**
     * 索引名称
     */
    private String index;

    /**
     * 文档类型
     */
    private String type;

    /**
     * 重试次数（仅用于更新操作）
     */
    private Integer retryOnConflict;

    /**
     * 文档内容或更新脚本
     */
    private Map<String, Object> source;

    /**
     * 是否为脚本更新
     */
    private boolean isScript;
}