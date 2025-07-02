package com.wenqi.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类，用于配置静态资源访问路径
 * @author liangwenqi
 * @date 2025/7/2
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加静态资源映射，支持 /static/ 路径访问
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 保持默认的根路径访问方式
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
