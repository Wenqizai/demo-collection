package com.wenqi.example.number;

import java.math.BigDecimal;

/**
 * @author liangwenqi
 * @date 2023/11/30
 */
public class BigDecimalScaleTest {
    public static void main(String[] args) {
        System.out.println(new BigDecimal("1.1").setScale(0,  BigDecimal.ROUND_HALF_UP));
        System.out.println(new BigDecimal("1.1").setScale(0,  BigDecimal.ROUND_HALF_UP).intValue());
        System.out.println(new BigDecimal("1.1").intValue());
        System.out.println("#########");

        System.out.println(new BigDecimal("1.4").setScale(0,  BigDecimal.ROUND_HALF_UP));
        System.out.println(new BigDecimal("1.4").setScale(0,  BigDecimal.ROUND_HALF_UP).intValue());
        System.out.println(new BigDecimal("1.4").intValue());
        System.out.println("#########");

        System.out.println(new BigDecimal("1.5").setScale(0,  BigDecimal.ROUND_HALF_UP));
        System.out.println(new BigDecimal("1.5").setScale(0,  BigDecimal.ROUND_HALF_UP).intValue());
        System.out.println(new BigDecimal("1.5").intValue());
        System.out.println("#########");

        System.out.println(new BigDecimal("1.9").setScale(0,  BigDecimal.ROUND_HALF_UP));
        System.out.println(new BigDecimal("1.9").setScale(0,  BigDecimal.ROUND_HALF_UP).intValue());
        System.out.println(new BigDecimal("1.9").intValue());
        System.out.println("#########");

        System.out.println(new BigDecimal("1.99").setScale(1,  BigDecimal.ROUND_FLOOR));
        System.out.println(new BigDecimal("1.92").setScale(1,  BigDecimal.ROUND_DOWN));
        System.out.println(new BigDecimal("1.90").setScale(1,  BigDecimal.ROUND_DOWN));
        System.out.println(new BigDecimal("1.99").setScale(1,  BigDecimal.ROUND_DOWN));
        System.out.println("#########");

        System.out.println(new BigDecimal("-1.99").setScale(1,  BigDecimal.ROUND_DOWN));
        System.out.println(new BigDecimal("-1.99").setScale(1,  BigDecimal.ROUND_FLOOR));

    }
}
