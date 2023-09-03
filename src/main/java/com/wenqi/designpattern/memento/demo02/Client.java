package com.wenqi.designpattern.memento.demo02;

/**
 * @author Wenqi Liang
 * @date 9/3/2023
 */
public class Client {
    public static void main(String[] args) {
        Original original = new Original();
        Caretaker managerMemento = new Caretaker();
        recordResults(original, managerMemento);
        System.out.println("恢复前目标类的记录值: " + original.toString());

        // 第一名
        Memento memento1 = managerMemento.get(1);
        original.getOriginalFromMemento(memento1);
        System.out.println("恢复第一名的成绩信息: " + original.toString());

        // 第三名
        Memento memento3 = managerMemento.get(3);
        original.getOriginalFromMemento(memento3);
        System.out.println("恢复第三名的成绩信息: " + original.toString());

        // 清空记录下一组
        managerMemento.remove();
    }

    private static void recordResults(Original original, Caretaker managerMemento) {
        original.setName("张三");
        original.setTimestamp(2330);
        // 创建备份类
        Memento memento1 = original.createMemento();
        // 备份类存入管理类中
        managerMemento.add(memento1);


        original.setName("李四");
        original.setTimestamp(2550);
        // 创建备份类
        Memento memento2 = original.createMemento();
        // 备份类存入管理类中
        managerMemento.add(memento2);


        original.setName("王五");
        original.setTimestamp(2560);
        // 创建备份类
        Memento memento3 = original.createMemento();
        // 备份类存入管理类中
        managerMemento.add(memento1);
    }
}
