package com.wenqi.string;

/**
 * @author liangwenqi
 * @date 2022/8/22
 */
public class TestToHex {
    public static void main(String[] args) {
        System.out.println(convertPriceIdToEsId("241025061374649200"));
    }

    public static String convertPriceIdToEsId(String eslId) {
        if (eslId.contains("-")){
            return eslId;
        }

        if (eslId.length() < 10){
            return null;
        }

        eslId = Long.toHexString(Long.parseLong(eslId.substring(eslId.length() - 10))).toUpperCase().replaceAll("(.{2})", "$1-");
        return eslId.substring(0, eslId.length()-1);
    }
}
