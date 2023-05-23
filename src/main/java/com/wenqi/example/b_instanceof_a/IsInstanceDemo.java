package com.wenqi.example.b_instanceof_a;

/**
 * @author liangwenqi
 * @date 2023/5/23
 */
public class IsInstanceDemo {
    public static void main(String[] args) {
        Shape shape = new Triangle();
        Triangle triangle = new Triangle();
        IsoscelesTriangle isoscelesTriangle = new IsoscelesTriangle();
        Triangle isoscelesTriangle2 = new IsoscelesTriangle();
        Shape nonspecificShape = null;

        // the right-hand side type is more generic than the left-hand side object
        // In particular, providing null to the isInstance method returns false.
        // 右边比左边更加特定
        System.out.println(Shape.class.isInstance(shape));  // true
        System.out.println(Shape.class.isInstance(triangle));  // true
        System.out.println(Shape.class.isInstance(isoscelesTriangle));  // true
        System.out.println(Shape.class.isInstance(isoscelesTriangle2)); // true
        System.out.println(Shape.class.isInstance(nonspecificShape)); // false

        System.out.println("#########################");

        System.out.println(Triangle.class.isInstance(shape));// true
        System.out.println(Triangle.class.isInstance(triangle));// true
        System.out.println(Triangle.class.isInstance(isoscelesTriangle));// true
        System.out.println(Triangle.class.isInstance(isoscelesTriangle2));// true

        System.out.println("#########################");

        System.out.println(IsoscelesTriangle.class.isInstance(shape));// false
        System.out.println(IsoscelesTriangle.class.isInstance(triangle));// false
        System.out.println(IsoscelesTriangle.class.isInstance(isoscelesTriangle));// true
        System.out.println(IsoscelesTriangle.class.isInstance(isoscelesTriangle2));// true
    }
}
