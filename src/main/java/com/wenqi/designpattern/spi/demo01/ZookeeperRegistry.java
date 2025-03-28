package com.wenqi.designpattern.spi.demo01;

/**
 * @author Wenqi Liang
 * @date 2024/11/3
 */
public class ZookeeperRegistry implements Registry{
    @Override
    public void register(String url) {
        System.out.printf("Register %s service to zookeeper%n", url);
    }
}
