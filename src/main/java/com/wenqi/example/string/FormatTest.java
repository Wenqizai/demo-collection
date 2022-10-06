package com.wenqi.string;


import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author liangwenqi
 * @date 2021/9/27
 */
public class FormatTest {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        //System.out.println(String.format("%04d", 200));

        //CyclicBarrier cyclicBarrier = new CyclicBarrier(20);
        //for (int i = 0; i < 1000; i++) {
        //    new Thread(() -> {
        //        int number = RandomUtils.nextInt(0, 999);
        //        try {
        //            cyclicBarrier.await();
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        } catch (BrokenBarrierException e) {
        //            e.printStackTrace();
        //        }
        //        System.out.println(fillZeroFromLeft(String.valueOf(number), 6) + "->" + System.currentTimeMillis());
        //    }).start();
        //}

        System.out.println(fillZeroFromLeft("11", 6));
    }


    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }

    public static String fillZeroFromLeft(String source, int totalLength) {
        int strLen = source.length();
        if (strLen < totalLength) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < totalLength - strLen; i++) {
                stringBuilder.append(0);
            }
            stringBuilder.append(source);
            source = stringBuilder.toString();
        }
        return source;
    }
}
