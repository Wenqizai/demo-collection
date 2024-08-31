package com.wenqi.example.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * @author liangwenqi
 * @date 2023/4/14
 */
public class HashSetAddNullTest {
    public static void main(String[] args) {
        Set<Object> set = new HashSet<>();
        set.addAll(null);
    }
}
