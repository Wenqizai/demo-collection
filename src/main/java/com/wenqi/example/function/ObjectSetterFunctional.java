package com.wenqi.example.function;

/**
 * @author liangwenqi
 * @date 2023/11/24
 */
public class ObjectSetterFunctional {
    // 定义一个函数接口，表示一个接受两个参数的操作
    @FunctionalInterface
    interface SetterFunction<T, U> {
        void set(T obj, U value);
    }

    // 泛型方法，接受一个SetterFunction，并调用它的set方法设置属性值
    public static <T, U> void setValue(T obj, SetterFunction<T, U> setter, U value) {
        setter.set(obj, value);
    }

    // 泛型方法，接受一个SetterFunction，并调用它的set方法设置属性值
    public static <T, U> void setNullValue(T obj, SetterFunction<T, U> setter) {
        setter.set(obj, null);
    }

    public static void main(String[] args) {
        // 示例对象
        Student myObject = new Student();

        // 使用函数式编程设置属性值
        setValue(myObject, Student::setName, "John");
        setValue(myObject, Student::setAge, 25);

        // 打印结果
        System.out.println(myObject.getName());  // 输出: John
        System.out.println(myObject.getAge());   // 输出: 25


        // 使用函数式编程设置属性值
        setNullValue(myObject, Student::setName);
        setNullValue(myObject, Student::setAge);

        // 打印结果
        System.out.println(myObject.getName());  // 输出: John
        System.out.println(myObject.getAge());   // 输出: 25
    }

    public static class Student {
        private String name;
        private Integer age;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }
}
