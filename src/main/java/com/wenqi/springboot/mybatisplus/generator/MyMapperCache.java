package com.wenqi.springboot.mybatisplus.generator;

import org.apache.ibatis.cache.Cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Wenqi Liang
 * @date 2024/4/8
 */
public class MyMapperCache implements Cache {
    public MyMapperCache(String id) {
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void putObject(Object o, Object o1) {

    }

    @Override
    public Object getObject(Object o) {
        return null;
    }

    @Override
    public Object removeObject(Object o) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return Cache.super.getReadWriteLock();
    }
}
