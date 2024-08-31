package com.wenqi.example.collection.list;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author liangwenqi
 * @date 2023/11/6
 */
public class RetainTest {
    public static void main(String[] args) {
        List<String> list1 = Lists.newArrayList("1", "1","1");
        List<String> list2 = Lists.newArrayList("2", "3");

        list1.retainAll(list2);
        System.out.println(list1);
    }
}
