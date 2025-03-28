package com.wenqi.springboot.elasticsearch.model;

import lombok.Data;

/**
 * 博客实体类
 */
@Data
public class Blog {
    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客内容
     */
    private String text;

    /**
     * 发布日期
     */
    private String date;
}