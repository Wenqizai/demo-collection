package com.wenqi.book.geektimebasemath;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wenqi Liang
 * @date 2023/10/16
 */
public class Lesson5_2 {

    public static void main(String[] args) {
        positiveGet(1, new ArrayList<>());
    }

    public static long[] rewards = {1, 2, 4, 8};
    public static long count;

    public static void positiveGet(long total, List<Long> results) {
        if (total == 8) {
            if (results.size() == 1) {
                results.add(1L);
            }
            System.out.println("found one, count : " + ++count + " results : " + JSON.toJSONString(results));
        } else if (total < 8) {
            for (long reward : rewards) {
                long temp = total * reward;
                if (reward == 1 && !results.isEmpty()) {
                    continue;
                } else if (temp > 8) {
                    break;
                }
                List<Long> newResults = new ArrayList<>(results);
                newResults.add(reward);
                positiveGet(temp, newResults);
            }
        }
    }
}
