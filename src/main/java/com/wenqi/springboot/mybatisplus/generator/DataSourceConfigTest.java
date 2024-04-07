package com.wenqi.springboot.mybatisplus.generator;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;

/**
 * @author liangwenqi
 * @date 2024/4/7
 */
public class DataSourceConfigTest {
    private static final String url = "jdbc:mysql://10.0.88.8:3306/test_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true";
    private static final String username = "root";
    private static final String password = "root";


    public static void main(String[] args) {
        test();
    }

    public static void test() {
        new DataSourceConfig.Builder(url, username, password).build();
    }

    public static void test2() {
    }
}
