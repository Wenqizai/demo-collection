package com.wenqi.springboot.center.thread.transaction;

import com.wenqi.springboot.mapper.SpringBootRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 没有复现死锁
 * 
 * @author Wenqi Liang
 * @date 2023/11/27
 */
@Service
public class MultiUpdateDeadLock {
    @Autowired
    private SpringBootRoleMapper springBootRoleMapper;


//    @Transactional
    public void testUpdateDeadLock() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("Transaction" + i);
        }
        springBootRoleMapper.batchUpdate(list);
    }
}
