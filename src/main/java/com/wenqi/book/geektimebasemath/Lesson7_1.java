package com.wenqi.book.geektimebasemath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排列法, 田忌赛马
 * @author liangwenqi
 * @date 2023/10/19
 */
public class Lesson7_1 {

    public static void main(String[] args) {
        List<String> t_horses = new ArrayList<>(Arrays.asList("t1", "t2", "t3"));
        Lesson7_1.permutate(t_horses, new ArrayList<>());

    }

    /**
     * 设置齐王的马跑完所需时间
     */
    public static Map<String, Double> q_horses_time = new HashMap<String, Double>() {
        {
            put("q1", 1.0);
            put("q2", 2.0);
            put("q3", 3.0);
        }
    };

    /**
     * 设置田忌的马跑完所需时间
     */
    public static Map<String, Double> t_horses_time = new HashMap<String, Double>() {
        {
            put("t1", 1.5);
            put("t2", 2.5);
            put("t3", 3.5);
        }
    };

    public static List<String> q_horses = new ArrayList<>(Arrays.asList("q1", "q2", "q3"));

    public static void permutate(List<String> t_horses, List<String> result) {
        if (t_horses.isEmpty()) {
            System.out.println(result);
            compare(result, q_horses);
            System.out.println();
            return;
        }

        for (int i = 0; i < t_horses.size(); i++) {
            List<String> rest_horses = new ArrayList<>(t_horses);
            List<String> new_result = new ArrayList<>(result);
            new_result.add(t_horses.get(i));
            rest_horses.remove(t_horses.get(i));
            permutate(rest_horses, new_result);
        }


    }

    private static void compare(List<String> tHorses, List<String> qHorses) {
       int t_won_cnt = 0;
        for (int i = 0; i < tHorses.size(); i++) {
            Double t_time = t_horses_time.get(tHorses.get(i));
            Double q_time = q_horses_time.get(qHorses.get(i));
            System.out.println(t_time + " " + q_time);
            if (t_time < q_time) {
                t_won_cnt++;
            }
        }
        if (t_won_cnt > (tHorses.size() / 2)) {
            System.out.println("田忌获胜! \n");
        } else {
            System.out.println("齐王获胜! \n");
        }

    }

}
