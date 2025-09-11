package com.wenqi.example.string;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangwenqi
 * @date 2022/1/12
 */
public class StringUtilTest {
    public static void main(String[] args) {
//        testContains();

        testAllBlank();
    }

    private static void testContains() {
        //List<String> list = new ArrayList<>();
        //System.out.println(StringUtils.join(list, ","));

        System.out.println("国产香蕉(W)".contains("W"));
        System.out.println(Integer.valueOf('a'));
    }


    public static void testAllBlank() {
        System.out.println(StringUtils.isAllBlank(null, null));
        System.out.println(StringUtils.isAllBlank(null, ""));
        System.out.println(StringUtils.isAllBlank("", ""));
    }

}
