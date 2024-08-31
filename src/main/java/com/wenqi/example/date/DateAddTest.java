package com.wenqi.example.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author liangwenqi
 * @date 2024/2/19
 */
public class DateAddTest {

    public static void main(String[] args) {
        Date date = new Date();
        Date dateAdd = getDateAdd(date, -1);
        System.out.println(dateAdd);
    }

    public static Date getDateAdd(Date date, int amount) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(GregorianCalendar.DATE, amount);
        return cal.getTime();
    }
}
