package com.wenqi.example.primitive;

/**
 * 小数的二进制存储方式:
 * <p>
 * 32位 float  : 符号位 1 - 指数位 8 - 位数位 23
 * 64位 double : 符号位 1 - 指数位 11 - 位数位 52
 * <p>
 *
 * 具体求法: 例如 float a = 0.65f;
 * 1. 求 0.75 的二进制表示形式(乘2正序表示法) -> 0.010(-28)
 * 2. 尾数部分表示整数第一位为1, 第二位为0, 故0.010需要左移2位才符合小数正则表达式要求1.0
 * 3. 小数的正则表示法和excess系统表示法: 0.010 = 1.0 * 2 ^ -2
 * 4. 指数为-2用EXCESS系统表示位为125 (125 - 127 = -2), 125的2进制为 0111 1101
 * 5. 即计算出来的2进制存储是 : 0 - 0111 1101 - 0(-22) (符号位 - 指数位 - 尾数位)
 *
 * @author Wenqi Liang
 * @date 2022/10/15
 */
public class FloatTest {

    public static void main(String[] args) {
        float a = 0.25f;
        String string = Integer.toBinaryString(Float.floatToIntBits(a));
        System.out.println(string);
        System.out.println(string.length());
    }
}
