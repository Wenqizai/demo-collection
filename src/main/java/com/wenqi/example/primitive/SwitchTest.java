package com.wenqi.example.primitive;

/**
 * @author liangwenqi
 * @date 2024/4/11
 */
public class SwitchTest {
    public static void main(String[] args) {
        extracted(1);
        // bug: require not null
        extracted(null);
    }

    private static void extracted(Integer num) {
        switch (num) {
            case 1:
                System.out.println("1");
                break;
            case 2:
                System.out.println("2");
                break;
            default:
                System.out.println("default");
        }
    }
}
