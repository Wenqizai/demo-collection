package com.wenqi.designpattern.bridge.demo02.device;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public interface Device {
    boolean isEnabled();

    void enable();

    void disable();

    int getVolume();

    void setVolume(int volume);

    int getChannel();

    void setChannel(int channel);

    void printStatus();
}
