package com.wenqi.springboot.elasticsearch.model;

import lombok.Data;

/**
 * 统一API响应结果
 */
@Data
public class ResponseResult<T> {
    /**
     * 状态码：0-失败，1-成功
     */
    private Integer state;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 创建成功响应
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setState(1);
        result.setData(data);
        return result;
    }

    /**
     * 创建失败响应
     */
    public static <T> ResponseResult<T> fail(String msg) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setState(0);
        result.setMsg(msg);
        return result;
    }
}