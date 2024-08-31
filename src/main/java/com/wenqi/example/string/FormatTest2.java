package com.wenqi.string;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangwenqi
 * @date 2021/11/3
 */
public class FormatTest2 {
    public static void main(String[] args) {
        List<String> skuCodeList = new ArrayList<>();
        skuCodeList.add("1");
        skuCodeList.add("1");
        skuCodeList.add("1");
        skuCodeList.add("1");
        System.out.println(String.format("数组: %s", skuCodeList));
    }
}
