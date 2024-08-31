package com.wenqi.designpattern.action.a2.view;

import com.google.gson.Gson;
import com.wenqi.designpattern.action.a2.utils.Aggregator;
import com.wenqi.designpattern.action.a2.dto.RequestInfo;
import com.wenqi.designpattern.action.a2.dto.RequestStat;
import com.wenqi.designpattern.action.a2.storage.MetricsStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Wenqi Liang
 * @date 2022/10/16
 */
public class ConsoleReporter {
    private MetricsStorage metricsStorage;
    private ScheduledExecutorService executor;

    public ConsoleReporter(MetricsStorage metricsStorage) {
        this.metricsStorage = metricsStorage;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void startRepeatedReport(long periodInSeconds, long durationInSeconds) {
        // 定时触发任务
        executor.scheduleAtFixedRate(() -> {
            // 1. 根据给定的时间区间, 从数据库中拉去数据
            long durationInMillis = durationInSeconds * 1000;
            long endTimeInMillis = System.currentTimeMillis();
            long startTimeInMillis = endTimeInMillis - durationInMillis;
            Map<String, List<RequestInfo>> requestInfos = metricsStorage.getRequestInfos(startTimeInMillis, endTimeInMillis);
            Map<String, RequestStat> stats = new HashMap<>();
            for (Map.Entry<String, List<RequestInfo>> entry : requestInfos.entrySet()) {
                String apiName = entry.getKey();
                List<RequestInfo> requestInfosPerApi = entry.getValue();
                // 2. 根据原始数据, 计算得到统计数据
                RequestStat requestStat = Aggregator.aggregate(requestInfosPerApi, durationInMillis);
                stats.putIfAbsent(apiName, requestStat);
            }

            // 3. 将统计数据显示到终端(命令行或邮件)
            System.out.println("Time Span: [" + startTimeInMillis + "," + endTimeInMillis);
            System.out.println(new Gson().toJson(stats));
        }, 0,periodInSeconds, TimeUnit.SECONDS);
    }

}
