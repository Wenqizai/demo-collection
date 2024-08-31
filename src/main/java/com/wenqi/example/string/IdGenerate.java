package com.wenqi.string;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liangwenqi
 * @date 2022/9/27
 */
public class IdGenerate {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final String PATTERN = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String COMPOSE_ORDER_BUSINESS_CODE = "C";


    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            // C + 7位num的36进制
            System.out.println(COMPOSE_ORDER_BUSINESS_CODE
                    + getThirtySevenByte(Integer.parseInt("220928"), 4)
                    + getThirtySevenByte(counter.getAndIncrement(), 3));
        }
    }

    public static String getCurrentDateString(String dateFormat) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(cal.getTime()).trim();
    }

    public static String getThirtySevenByte(Integer num, int level) {
        /*
         * 这里的逻辑，是传入数字和要生成的位数
         * 不断地拿0-9A-Z中的值，最终反转,拼接为单号
         *
         */
        int count = 0;
        int current = num;

        StringBuilder result = new StringBuilder();
        while(count < level && current >= 0) {
            result.append(PATTERN.charAt(current % 36));
            current = current / 36;
            count ++;
        }
        result.setLength(level);
        result.reverse();
        return result.toString();
    }

}
