package com.wenqi.example.date;

import java.util.Calendar;

/**
 * @author liangwenqi
 * @date 2023/11/28
 */
public class MonthOfDay {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.MONTH, calendar2.get(Calendar.MONTH) - 1);
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));

        System.out.println(calendar.getTime());
        System.out.println(calendar2.getTime());

        System.out.println("#############33");

        // 会再减一个月
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        System.out.println(calendar.getTime());

    }
}
