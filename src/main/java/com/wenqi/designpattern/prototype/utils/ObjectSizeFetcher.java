package com.wenqi.designpattern.prototype.utils;

import java.lang.instrument.Instrumentation;

/**
 * @author liangwenqi
 * @date 2023/10/26
 */
public class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
}
