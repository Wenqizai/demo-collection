package com.wenqi.springboot.elasticsearch.model;

import lombok.Data;

import java.util.Map;

/**
 * 脚本更新请求对象，用于封装脚本内容和参数
 */
@Data
public class ScriptUpdateRequest {
    /**
     * update script
     */
    private String script;
    /**
     * 相关参数
     */
    private Map<String, Object> params;
}