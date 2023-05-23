package com.wenqi.example.b_instanceof_a;

/**
 * https://www.baeldung.com/java-isinstance-isassignablefrom
 *
 * @author liangwenqi
 * @date 2023/5/23
 */
public class InstanceOfDemo {
    public static void main(String[] args) {
        Shape shape = new Triangle();
        Triangle triangle = new Triangle();
        IsoscelesTriangle isoscelesTriangle = new IsoscelesTriangle();
        Shape nonspecificShape = null;

        // the right-hand side type is more generic than the left-hand side object
        // More specifically, the instanceof operator will process null values to false.
        // 右边比左边更加通用, 子 instanceof 父
        System.out.println(shape instanceof Shape); // true
        System.out.println(triangle instanceof Shape); // true
        System.out.println(isoscelesTriangle instanceof Shape); // true
        System.out.println(nonspecificShape instanceof Shape); // false

        System.out.println("#########################");

        System.out.println(shape instanceof Triangle); // true
        System.out.println(triangle instanceof Triangle); // true
        System.out.println(isoscelesTriangle instanceof Triangle); // true
        System.out.println(nonspecificShape instanceof Triangle); // false

        System.out.println("#########################");

        System.out.println(shape instanceof IsoscelesTriangle); // false
        System.out.println(triangle instanceof IsoscelesTriangle); // false
        System.out.println(isoscelesTriangle instanceof IsoscelesTriangle); // true
        System.out.println(nonspecificShape instanceof IsoscelesTriangle); // false
    }
}
