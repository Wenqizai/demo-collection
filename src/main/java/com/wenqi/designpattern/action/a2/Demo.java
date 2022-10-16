package com.wenqi.designpattern.action.a2;

import com.wenqi.designpattern.action.a2.dto.RequestInfo;
import com.wenqi.designpattern.action.a2.storage.RedisMetricsStorage;
import com.wenqi.designpattern.action.a2.view.ConsoleReporter;
import com.wenqi.designpattern.action.a2.view.EmailReporter;

/**
 * @author Wenqi Liang
 * @date 2022/10/16
 */
public class Demo {
    public static void main(String[] args) {
        RedisMetricsStorage storage = new RedisMetricsStorage();
        ConsoleReporter consoleReporter = new ConsoleReporter(storage);
        consoleReporter.startRepeatedReport(60, 60);

        EmailReporter emailReporter = new EmailReporter(storage);
        emailReporter.addToAddress("xxx@123.com");
        emailReporter.startDailyReport();

        MetricsCollector collector = new MetricsCollector(storage);
        collector.recordRequest(new RequestInfo("register", 123, 10234));
        collector.recordRequest(new RequestInfo("register", 223, 11234));
        collector.recordRequest(new RequestInfo("register", 323, 12334));
        collector.recordRequest(new RequestInfo("login", 23, 12434));
        collector.recordRequest(new RequestInfo("login", 1223, 14234));

        try {
            Thread.sleep(100 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
