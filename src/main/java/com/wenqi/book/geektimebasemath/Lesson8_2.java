package com.wenqi.book.geektimebasemath;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 假设现在需要设计一个抽奖系统。需要依次从100个人中，抽取三等奖10名，二等奖3名和一等奖1名。请列出所有可能的组合，需要注意的每人最多只能被抽中1次。
 *
 * @author liangwenqi
 * @date 2023/10/25
 */
public class Lesson8_2 {
    private static int count = 0;

    public static void main(String[] args) {
        lottery(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), Lists.newArrayList(), 3);
    }

    public static void lottery(List<Integer> rest, List<Integer> result, int size) {
        if (result.size() == size) {
            System.out.println(JSON.toJSONString(result) + "=> count: " + ++count);
            return;
        }

        for (int i = 0; i < rest.size(); i++) {
            List<Integer> newRest = new ArrayList<>(rest);
            List<Integer> newResult = new ArrayList<>(result);
            newResult.add(rest.get(i));
            newRest.remove(rest.get(i));
            lottery(newRest, newResult, size);
        }
    }
}
