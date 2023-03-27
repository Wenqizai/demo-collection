package com.wenqi.example.array;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author liangwenqi
 * @date 2023/3/27
 */
public class LinkedListPollTest {
    public static void main(String[] args) {
        String img = "1,2,3,4";
        LinkedList<String> linkedList = Lists.newLinkedList(Arrays.asList(img.split(",")));
        for (int i = 0; i < 5; i++) {
            System.out.println(linkedList.pollFirst());
        }
    }
}
