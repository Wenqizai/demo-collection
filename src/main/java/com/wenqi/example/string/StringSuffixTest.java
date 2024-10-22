package com.wenqi.example.string;

/**
 * @author liangwenqi
 * @date 2024/10/22
 */
public class StringSuffixTest {
    public static void main(String[] args) {
        test01("1.xlsx");
        test01("1");
        test01("1.pdf");
        test01("test.xlsx.xlsx");
    }


    private static void test01(String fileName) {
        boolean flag = fileName.endsWith(".xlsx");
        if (!flag) {
            fileName = fileName + ".xlsx";
        }
        System.out.println("fileName = " + fileName + ", endWith = " + flag);
    }
}
