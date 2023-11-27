package com.wenqi.designpattern.prototype.utils;

import com.wenqi.designpattern.prototype.hotword.SearchWord;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import org.openjdk.jol.info.ClassLayout;

/**
 * @author liangwenqi
 * @date 2023/10/26
 */
public class DemoTest {
    public static void main(String[] args) {
        // 1.
        System.out.println(ObjectSizeCalculator.getObjectSize(new Object()));

        // 2. jol-core 工具类
        System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
        System.out.println(ClassLayout.parseInstance(new SearchWord("1", 1, 1)).toPrintable());
    }
}
