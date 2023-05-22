package com.wenqi.example.reflect.methodhandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * since JDK 1.7,  java.lang.invoke 包中新增了 MethodHandle 这个类
 * MethodHandle的基本功能与反射中的Method类似, 当它相比反射更加灵活.
 *
 * 反射是 Java API 层面支持的一种机制，MethodHandle 则是 JVM 层支持的机制，相较而言，反射更加重量级，MethodHandle 则更轻量级，性能也比反射更好些。
 *
 * @author liangwenqi
 * @date 2023/5/22
 */
public class MethodHandleDemo {
    /**
     * 定义供反射调用的方法
     */
    public String sayHello(String str) {
        return "Hello, " + str;
    }

    public static void main(String[] args) throws Throwable {
        // 初始化MethodHandleDemo实例
        SubMethodHandleDemo subMethodHandleDemo = new SubMethodHandleDemo();
        // 定义sayHello()方法的签名，第一个参数是方法的返回值类型，第二个参数是方法的参数列表
        MethodType methodType = MethodType.methodType(String.class, String.class);
        // 根据方法名和MethodType在MethodHandleDemo中查找对应的MethodHandle
        MethodHandle methodHandle = MethodHandles.lookup()
                .findVirtual(MethodHandleDemo.class, "sayHello", methodType);
        // 将MethodHandle绑定到一个对象上，然后通过invokeWithArguments()方法传入实参并执行
        System.out.println(methodHandle.bindTo(subMethodHandleDemo).invokeWithArguments("MethodHandleDemo"));
        // 下面是调用MethodHandleDemo对象(即父类)的方法
        MethodHandleDemo methodHandleDemo = new MethodHandleDemo();
        System.out.println(methodHandle.bindTo(methodHandleDemo).invokeWithArguments("MethodHandleDemo"));
    }


    public static class SubMethodHandleDemo extends MethodHandleDemo{
        // 定义一个sayHello()方法
        @Override
        public String sayHello(String s) {
            return "Sub Hello, " + s;
        }

    }
}
