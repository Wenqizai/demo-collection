package com.wenqi.designpattern.adapter;

/**
 * 对象适配器
 * @author liangwenqi
 * @date 2021/8/9
 */
public class ObjectAdaptor implements ITarget {
    private Adaptee adaptee;

    public ObjectAdaptor(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void f1() {
        adaptee.fa();
    }

    @Override
    public void f2() {
        adaptee.fb();
    }

    @Override
    public void f3() {
        adaptee.fc();
    }
}
