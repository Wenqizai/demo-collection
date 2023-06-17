package com.wenqi.test.mybatis.cache.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @author Wenqi Liang
 * @date 2023/6/17
 */
public interface ClassMapper {
    public int updateClassName(@Param("name") String className, @Param("id") int id);
}
