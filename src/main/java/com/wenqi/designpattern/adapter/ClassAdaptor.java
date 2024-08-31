package com.wenqi.designpattern.adapter;

/**
 * 类适配器
 * @author liangwenqi
 * @date 2021/8/9
 */
public class ClassAdaptor extends Adaptee implements ITarget {

    public void f1() {
        super.fa();
    }

    public void f2() {
        super.fb();
    }

    public void f3() {
        super.fc();
    }

}
