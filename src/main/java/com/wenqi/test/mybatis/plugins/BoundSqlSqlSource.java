package com.wenqi.test.mybatis.plugins;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @author liangwenqi
 * @date 2023/11/14
 */
public class BoundSqlSqlSource implements SqlSource {
    private final BoundSql boundSql;

    public BoundSqlSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }
}
