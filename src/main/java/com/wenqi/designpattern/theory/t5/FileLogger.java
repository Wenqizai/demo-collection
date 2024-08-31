package com.wenqi.designpattern.theory.t5;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Wenqi Liang
 * @date 2022/10/5
 */
public class FileLogger extends Logger {
    private Writer fileWriter;

    public FileLogger(String name, boolean enabled, LeveL minPermittedLevel, String filePath) throws IOException {
        super(name, enabled, minPermittedLevel);
        this.fileWriter = new FileWriter(filePath);
    }


    @Override
    protected void doLog(LeveL leveL, String message) throws IOException {
        // 格式化 level 和 message, 输出到日志文件
        fileWriter.write(message);
    }
}
