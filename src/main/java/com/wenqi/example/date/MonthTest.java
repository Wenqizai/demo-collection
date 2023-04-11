package com.wenqi.example.date;

import java.util.Calendar;
import java.util.Date;

/**
 * @author liangwenqi
 * @date 2023/4/11
 */
public class MonthTest {
    public static void main(String[] args) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        System.out.println(String.valueOf(instance.get(Calendar.MONTH)));
    }
}
