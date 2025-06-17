package com.wenqi.example.string;

/**
 * @author liangwenqi
 * @date 2021/10/13
 */
public class NumericTest {
    public static void main(String[] args) {
        //System.out.println(isNumeric("-1"));

        System.out.println(getCheckCode("2000006"));
    }

    public static boolean isNumeric(final CharSequence cs) {
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String getCheckCode(String code) {
        int c1 = 0, c2 = 0;
        for (int i = 0; i < code.length(); i += 2) {
            char c = code.charAt(i);
            int n = c - '0';
            c1 += n;
        }
        for (int i = 1; i < code.length(); i += 2) {
            char c = code.charAt(i);
            int n = c - '0';
            c2 += n;
        }
        return (10 - (c1 + c2 * 3) % 10) % 10 + "";
    }
}
