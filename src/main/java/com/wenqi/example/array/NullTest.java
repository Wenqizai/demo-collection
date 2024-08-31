package com.wenqi.example.array;

import java.util.ArrayDeque;

/**
 * @author Wenqi Liang
 * @date 2022/10/6
 */
public class NullTest {
    public static void main(String[] args) {
        ArrayDeque<Object> arrayDeque = new ArrayDeque<>(16);
        arrayDeque.add(1);
        arrayDeque.add("null");
        // 不允许添加 null 元素到 ArrayDeque 中
        arrayDeque.add(null);
        System.out.println(arrayDeque.peekFirst());
        System.out.println(arrayDeque.peekLast());

        for (Object o : arrayDeque) {
            System.out.println(o);
        }
    }

    private static void arrayNullTest() {
        Object[] objects = new Object[16];
        objects[0] = 1;
        objects[1] = "abc";
        // 数组里面可以放null
        objects[2] = null;
        for (int i = 0; i < objects.length; i++) {
            System.out.println(objects[i]);
        }
    }
}
