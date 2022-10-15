package com.wenqi.string;

import java.lang.reflect.Field;

/**
 * @author liangwenqi
 * @date 2022/6/15
 */
public class ReflectTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        String s = "a";
        Field value = String.class.getDeclaredField("value");
        value.setAccessible(true);
        byte[] bytes = (byte[]) value.get(s);
        bytes[0] = 66;
        // //这里 s 输出的就是 66 对应的字符 B
        System.out.println(s);
    }
}
