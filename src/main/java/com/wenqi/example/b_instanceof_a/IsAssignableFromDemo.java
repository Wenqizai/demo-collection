package com.wenqi.example.b_instanceof_a;

/**
 * @author liangwenqi
 * @date 2023/5/23
 */
public class IsAssignableFromDemo {
    public static void main(String[] args) {
        Shape shape = new Triangle();
        Triangle triangle = new Triangle();
        IsoscelesTriangle isoscelesTriangle = new IsoscelesTriangle();
        Triangle isoscelesTriangle2 = new IsoscelesTriangle();

        // the right-hand side must be either the same or more specific than the left-hand side
        // We can also see that we're never able to assign our Shape interface.
        // 右边比左边更加特定, Shape.class 永远返回false
        System.out.println(shape.getClass().isAssignableFrom(Shape.class)); // false
        System.out.println(shape.getClass().isAssignableFrom(shape.getClass())); // true
        System.out.println(shape.getClass().isAssignableFrom(triangle.getClass())); // true
        System.out.println(shape.getClass().isAssignableFrom(isoscelesTriangle.getClass())); // true
        System.out.println(shape.getClass().isAssignableFrom(isoscelesTriangle2.getClass())); // true

        System.out.println("#########################");

        System.out.println(triangle.getClass().isAssignableFrom(Shape.class)); // false
        System.out.println(triangle.getClass().isAssignableFrom(shape.getClass())); // true
        System.out.println(triangle.getClass().isAssignableFrom(triangle.getClass())); // true
        System.out.println(triangle.getClass().isAssignableFrom(isoscelesTriangle.getClass())); // true
        System.out.println(triangle.getClass().isAssignableFrom(isoscelesTriangle2.getClass())); // true

        System.out.println("#########################");

        System.out.println(isoscelesTriangle.getClass().isAssignableFrom(Shape.class)); // false
        System.out.println(isoscelesTriangle.getClass().isAssignableFrom(shape.getClass())); // false
        System.out.println(isoscelesTriangle.getClass().isAssignableFrom(triangle.getClass())); // false
        System.out.println(isoscelesTriangle.getClass().isAssignableFrom(isoscelesTriangle.getClass())); // true
        System.out.println(isoscelesTriangle.getClass().isAssignableFrom(isoscelesTriangle2.getClass())); // true

        System.out.println("#########################");

        System.out.println(isoscelesTriangle2.getClass().isAssignableFrom(Shape.class)); // false
        System.out.println(isoscelesTriangle2.getClass().isAssignableFrom(shape.getClass())); // false
        System.out.println(isoscelesTriangle2.getClass().isAssignableFrom(triangle.getClass())); // false
        System.out.println(isoscelesTriangle2.getClass().isAssignableFrom(isoscelesTriangle.getClass())); // true
        System.out.println(isoscelesTriangle2.getClass().isAssignableFrom(isoscelesTriangle2.getClass())); // true
    }
}
