package com.wenqi.book.geektimebasemath;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wenqi Liang
 * @date 2023/10/16
 */
public class Lesson5_1 {
    public static void main(String[] args) {
//        negativeGet(10, new ArrayList<>());
        positiveGet(0, new ArrayList<>());
    }

    public static long[] rewards = {1, 2, 5, 10};    // 四种面额的纸币
    public static long count;

    /**
     * 正向获取
     */
    public static void positiveGet(long total, List<Long> results) {
        if (total == 10) {
            System.out.println("found one, count : " + ++count + " result : " + JSON.toJSONString(results));
        } else if (total < 10) {
            for (long reward : rewards) {
                List<Long> newResult = new ArrayList<>(results);
                newResult.add(reward);
                long temp = total + reward;
                if (temp > 10) {
                    break;
                }
                positiveGet(temp, newResult);
            }
        }
    }

    /**
     * 负向获取
     */
    public static void negativeGet(long total, List<Long> results) {
        if (total == 0) {
            System.out.println("found one, count : " + ++count + " result : " + JSON.toJSONString(results));
        } else if (total > 0) {
            // 继续寻找
            for (long reward : rewards) {
                List<Long> newResult = new ArrayList<>(results);
                newResult.add(reward);
                long temp = total - reward;
                if (temp < 0) {
                    break;
                }
                negativeGet(temp, newResult);
            }
        }
    }


}
