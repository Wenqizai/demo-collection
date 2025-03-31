package com.wenqi.springboot.elasticsearch.service;

import com.wenqi.springboot.elasticsearch.model.Blog;

import java.util.Map;
import java.util.Set;

/**
 * 网站服务接口
 */
public interface IWebsiteService {
    /**
     * 创建博客文档，如果文档已存在则创建失败
     *
     * @param id   文档ID
     * @param blog 博客内容
     * @return 是否创建成功
     */
    boolean createBlog(String id, Blog blog);

    /**
     * 更新博客文档，如果文档不存在则创建
     *
     * @param id   文档ID
     * @param blog 博客内容
     * @return 文档ID
     */
    String updateBlog(String id, Blog blog);

    /**
     * 获取博客文档，支持三种不同的获取方式：
     * 1. 获取完整文档 (?pretty)
     * 2. 获取指定字段 (?_source=field1,field2)
     * 3. 仅获取source内容
     *
     * @param id           文档ID
     * @param sourceFields 需要获取的字段，如果为null则获取全部字段
     * @param sourceOnly   是否仅获取source内容
     * @return 博客文档内容
     */
    Map<String, Object> getBlog(String id, Set<String> sourceFields, boolean sourceOnly);

    /**
     * 根据视图数量条件删除博客文档
     *
     * @param id    文档ID
     * @param count 视图数量条件
     * @return 是否执行了删除操作
     */
    boolean deleteBlogByViewCount(String id, int count);

    /**
     * 通用脚本更新方法
     *
     * @param id            文档ID
     * @param scriptContent 脚本内容
     * @param params        脚本参数
     * @return 更新结果，包含更新状态和版本信息
     */
    Map<String, Object> updateByScript(String id, String scriptContent, Map<String, Object> params);
}