package com.wenqi.designpattern.bridge.demo02.remote;

import com.wenqi.designpattern.bridge.demo02.device.Device;

/**
 * @author liangwenqi
 * @date 2023/5/30
 */
public class AdvancedRemote extends BasicRemote {
    public AdvancedRemote(Device device) {
        super(device);
    }

    public void mute() {
        System.out.println("Remote: mute");
        device.setVolume(0);
    }
}
