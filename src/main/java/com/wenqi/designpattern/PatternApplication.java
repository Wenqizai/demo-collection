package com.wenqi.designpattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author liangwenqi
 * @date 2022/3/9
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class PatternApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatternApplication.class, args);
    }
}
