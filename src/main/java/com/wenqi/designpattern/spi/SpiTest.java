package com.wenqi.designpattern.spi;

import com.wenqi.designpattern.spi.demo01.Registry;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Wenqi Liang
 * @date 2024/11/3
 */
public class SpiTest {
    public static void main(String[] args) {
        ServiceLoader<Registry> serviceLoader = ServiceLoader.load(Registry.class);
        Iterator<Registry> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            Registry registry = iterator.next();
            System.out.printf("class: %s %n", registry.getClass().getName());
            registry.register("SPI");
        }
    }
}
