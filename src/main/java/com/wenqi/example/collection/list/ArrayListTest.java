package com.wenqi.example.collection.list;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangwenqi
 * @date 2023/8/25
 */
public class ArrayListTest {
    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        // out of range
        System.out.println(list.get(0));
    }
}
