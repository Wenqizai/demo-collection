package com.wenqi.book.geektimebasemath;

import java.util.Objects;

/**
 * 暴力破解密码
 *
 * 性能优化点:
 *
 *  1. 考虑结果的缓存
 *  2. 相同排序的去重, 减少重复排序
 *
 * @author liangwenqi
 * @date 2023/10/19
 */
public class Lesson7_2 {
    public static void main(String[] args) {
        guessPassword("");
    }

    /**
     * 密码
     */
    private static final String HIT_PASSWORD = "aaee";

    private static final String DICT = "abcde";

    private static int tryCount = 0;

    /**
     * 重字典中选出4位组合
     */
    public static void guessPassword(String choosePasswords) {
        if (choosePasswords.length() == 4) {
            tryPassword(choosePasswords, "");
            return;
        }

        for (int i = 0; i < DICT.length(); i++) {
            guessPassword(choosePasswords + DICT.charAt(i));
        }

    }

    /**
     * 全排序对比
     */
    public static void tryPassword(String rest, String result) {
        if (result.length() == 4) {
            if (Objects.equals(result, HIT_PASSWORD)) {
                System.out.println("hit in : " + ++tryCount);
                return;
            }
            System.out.println("tryCount: " + ++tryCount + " result: " + result);
            return;
        }

        for (int i = 0; i < rest.length(); i++) {
            String newResult = result + rest.charAt(i);
            StringBuilder sb = new StringBuilder(rest);
            sb.deleteCharAt(i);
            tryPassword(sb.toString(), newResult);
        }

    }
}
