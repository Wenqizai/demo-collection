package com.wenqi.book.geektimebasemath;

import java.util.Objects;

/**
 * 暴力破解密码
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
    private static final String HIT_PASSWORD = "ajeq";

    private static final String DICT = "abcde";

    private static int tryCount = 0;

    public static void guessPassword(String choosePasswords) {
        // 选4位
        if (choosePasswords.length() == 4) {
            if (Objects.equals(choosePasswords, HIT_PASSWORD)) {
                System.out.println("hit");
            }
            System.out.println("tryCount: " + ++tryCount);
            return;
        }

        for (int i = 0; i < DICT.length(); i++) {
            guessPassword(choosePasswords + DICT.charAt(i));
            for (int j = 0; j < DICT.length(); j++) {
                guessPassword(choosePasswords + DICT.charAt(j));
                for (int k = 0; k < DICT.length(); k++) {
                    guessPassword(choosePasswords + DICT.charAt(k));
                    for (int l = 0; l < DICT.length(); l++) {
                        guessPassword(choosePasswords + DICT.charAt(l));
                    }
                }
            }
        }

    }
}
