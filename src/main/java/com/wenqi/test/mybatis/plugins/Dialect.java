package com.wenqi.test.mybatis.plugins;

/**
 * @author liangwenqi
 * @date 2023/11/13
 */
public interface Dialect {
    /**
     * 是否支持分页
     */
    boolean supportPage();

    /**
     * 获取分页sql, 拼接分页参数
     */
    String getPagingSql(String sql, int offset, int limit);
}
