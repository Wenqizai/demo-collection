package com.wenqi.book.geektimebasemath;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 递归求全组合
 *
 * @author liangwenqi
 * @date 2023/10/25
 */
public class Lesson8_1 {

    public static void main(String[] args) {
        combine2(Lists.newArrayList("t1", "t2", "t3"), Lists.newArrayList(), 2);
    }

    /**
     * 这种实现是不断裁剪 rest, 推送循环往前走, 实现了抽取组合而不重复
     */
    public static void combine2(List<String> rest, List<String> result, int size) {
        if (result.size() == size) {
            System.out.println(JSON.toJSONString(result));
            return;
        }

        for (int i = 0; i < rest.size(); i++) {
            List<String> newResult = new ArrayList<>(result);
            newResult.add(rest.get(i));
            List<String> newRest = new ArrayList<>(rest.subList(i + 1, rest.size()));
            combine2(newRest, newResult, size);
        }
    }

    /**
     * 这个实现没有去重, 比如 [t1, t2] 和 [t2, t1] 重复出现
     * 去重方案:
     * <li> 1. 获取所有的结果之后, 排序后去除重复, 如 [t2, t1] 排序后变成 [t1, t2], 该方案会耗费大量性能
     * <li> 2. 看到 [t2, t1] 时直接丢弃, 因为 t1 一定会出现在 t2 前面, 所以 [t2, t1] 可以直接丢弃
     */
    public static void combine(List<String> rest, List<String> result, int size) {
        if (result.size() == size) {
            System.out.println(JSON.toJSONString(result));
            return;
        }

        for (int i = 0; i < rest.size(); i++) {
            List<String> newRest = new ArrayList<>(rest);
            List<String> newResult = new ArrayList<>(result);
            newResult.add(newRest.get(i));
            newRest.remove(newRest.get(i));
            combine(newRest, newResult, size);
        }
    }
}
