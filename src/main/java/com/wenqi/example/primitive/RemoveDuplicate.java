package com.wenqi.example.primitive;


import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wenqi Liang
 * @date 2024/8/8
 */
public class RemoveDuplicate {
    public static void main(String[] args) {
        // 有序数组, 去掉重复元素 12234555 => 12345
        List<Integer> list = Lists.newArrayList(1, 2, 2, 3, 4, 5, 5, 5);

        List<Integer> resultList = new ArrayList<>();

        int duplicate = list.get(0);
        resultList.add(duplicate);
        for (int i = 1; i < list.size(); i++) {
            Integer num = list.get(i);
            if (duplicate != num) {
                resultList.add(num);
                duplicate = num;
            }
        }

        System.out.println(resultList);
    }
}
