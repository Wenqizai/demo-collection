package com.wenqi.example.string.performance;

/**
 * 关于 StringBuilder 不同使用方法, 消耗内存测试
 * 测试工具: jProfiler
 *
 * @author liangwenqi
 * @date 2024/7/10
 */
public class StringBuilderMemoryTest {
    public static void main(String[] args) {
        //testString01();
        testString02();
    }

    /**
     * 内存: 空闲 0.39 GB,  使用 0.36 GB, 提交 0.74 GB
     * CPU: 8.33%
     * GC: 0.4%
     */
    public static void testString01() {
        int i = 0;
        while (i < 100000000) {
            String s = "Hi hello bye" + i;
            i++;
        }
    }

    /**
     * 内存: 空闲 233.8 MB,  使用 8.89 MB, 提交 241.7 MB
     * CPU : 14.7%
     * GC: 0%
     */
    public static void testString02() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (i < 100000000) {
            sb.delete(0, sb.length());
            sb.append("Hi hello bye").append(i);
            i++;
        }
    }
}
