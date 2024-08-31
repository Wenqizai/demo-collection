package com.wenqi.example.string;


import org.apache.commons.lang3.StringUtils;

/**
 * @author liangwenqi
 * @date 2022/8/29
 */
public class SubStringTest {
    public static void main(String[] args) {
        subStr("1234");
    }

    private static void testGetPluCode() {
        System.out.println(getPluCode("12345608"));
        System.out.println(getPluCode("02345600"));
        System.out.println(getPluCode("20300678"));
        System.out.println(getPluCode("20000018"));
        System.out.println(getPluCode("20000851"));
        System.out.println(getPluCode("20019068"));
        System.out.println(getPluCode("20000008"));
        System.out.println(getPluCode("00000010"));
    }


    public static void subStr(String str) {
        System.out.println(str.length());
        if (str.length() > 3) {
            System.out.println(str.substring(0, 3));
        }
    }

    public static String getPluCode(String skuCode) {
        if (StringUtils.isEmpty(skuCode) || skuCode.length() < 7) {
            return skuCode;
        }
        int startIndex = 1;
        for (int i = 1; i < skuCode.length() - 2; i++) {
            if (skuCode.charAt(i) != '0') {
                break;
            }
            startIndex = i + 1;
        }
        return skuCode.substring(startIndex, 7);
    }
}
