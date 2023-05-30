package com.wenqi.designpattern.bridge.demo02;

import com.wenqi.designpattern.bridge.demo02.device.Device;
import com.wenqi.designpattern.bridge.demo02.device.Radio;
import com.wenqi.designpattern.bridge.demo02.device.Tv;
import com.wenqi.designpattern.bridge.demo02.remote.AdvancedRemote;
import com.wenqi.designpattern.bridge.demo02.remote.BasicRemote;

/**
 * 桥接模式:
 * Remote 是抽象部分, 客户端直接使用Remote完成功能, Remote组合了Device
 * Device 是具体的实现, 负责Remote的调用的具体实现
 *
 * 桥接对象为Remote的实现, 如BasicRemote, AdvancedRemote
 * 当客户端使用Remote时, 传入具体的Device, 来完成具体功能.
 *
 * @author liangwenqi
 * @date 2023/5/30
 */
public class Demo {
    public static void main(String[] args) {
        testDevice(new Tv());
        testDevice(new Radio());
    }

    public static void testDevice(Device device) {
        System.out.println("Tests with basic remote.");
        BasicRemote basicRemote = new BasicRemote(device);
        basicRemote.power();
        device.printStatus();

        System.out.println("Tests with advanced remote.");
        AdvancedRemote advancedRemote = new AdvancedRemote(device);
        advancedRemote.power();
        advancedRemote.mute();
        device.printStatus();
    }
}
