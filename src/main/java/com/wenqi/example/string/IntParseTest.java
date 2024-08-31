package com.wenqi.string;

import cn.hutool.core.convert.Convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author liangwenqi
 * @date 2022/6/13
 */
public class IntParseTest {
    private static final AtomicLong seq = new AtomicLong(0);

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(nodeKey());
            nodeKey();
            nodeKey();
            //System.out.println("a -> " + nodeKey());
            //System.out.println("b -> " + nodeKey());
            //System.out.println("c -> " + nodeKey());
        }
        //Collections.sort(list);
        System.out.println(list);
    }

    public static int nodeKey(){
        return (int)(seq.getAndIncrement() & (8 - 1));
    }

    private static void test() {
        BigDecimal bigDecimal = new BigDecimal("1");
        System.out.println(bigDecimal);
        String doubleValue = "3.14";
        System.out.println(Double.valueOf(doubleValue).intValue());
        System.out.println(Convert.toInt(doubleValue));
        System.out.println(Integer.parseInt(doubleValue));
        System.out.println(Integer.valueOf(doubleValue));
    }
}
