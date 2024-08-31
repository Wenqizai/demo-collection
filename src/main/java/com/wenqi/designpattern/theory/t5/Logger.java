package com.wenqi.designpattern.theory.t5;

import java.io.IOException;

/**
 * @author Wenqi Liang
 * @date 2022/10/5
 */
public abstract class Logger {
    private String name;
    private boolean enabled;
    private LeveL minPermittedLevel;

    public Logger(String name, boolean enabled, LeveL minPermittedLevel) {
        this.name = name;
        this.enabled = enabled;
        this.minPermittedLevel = minPermittedLevel;
    }

    public void log(LeveL leveL, String message) throws IOException {
        boolean loggable = enabled && (minPermittedLevel.iniValue() <= minPermittedLevel.iniValue);
        if (!loggable) {
            return;
        }
        doLog(leveL, message);
    }

    protected abstract void doLog(LeveL leveL, String message) throws IOException;
}
