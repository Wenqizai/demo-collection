package com.wenqi.example.reflect.getinterface;

/**
 * @author liangwenqi
 * @date 2023/4/25
 */
public class ImplementClass extends AbstractClass /*implements IAnyInterface*/ {

    @Override
    Object justForTest() {
        System.out.println("super class is abstract");
        return null;
    }
}
