package com.wenqi.example.reflect.getinterface;

/**
 * @author liangwenqi
 * @date 2023/4/25
 */
public class GetInterfaceDemo {
    /**
     * 获取class实现的接口, 不包括 abstract class
     */
    public static void main(String[] args) {
        Class<?>[] interfaces = ImplementClass.class.getInterfaces();
        System.out.println("finish...");
    }
}
