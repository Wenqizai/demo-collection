package com.wenqi.designpattern.action.a2.view;

import com.wenqi.designpattern.action.a2.utils.Aggregator;
import com.wenqi.designpattern.action.a2.dto.RequestInfo;
import com.wenqi.designpattern.action.a2.dto.RequestStat;
import com.wenqi.designpattern.action.a2.storage.MetricsStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Wenqi Liang
 * @date 2022/10/16
 */
public class EmailReporter {
    private static final Long DAY_HOURS_IN_SECONDS = 86400L;

    private MetricsStorage metricsStorage;
    private EmailSender emailSender;
    private List<String> toAddresses = new ArrayList<String>();

    public EmailReporter(MetricsStorage metricsStorage) {
        this(metricsStorage, new EmailSender());
    }

    public EmailReporter(MetricsStorage metricsStorage, EmailSender emailSender) {
        this.metricsStorage = metricsStorage;
        this.emailSender = emailSender;
    }

    public void addToAddress(String address) {
        toAddresses.add(address);
    }

    public void startDailyReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        calendar.add(Calendar.HOUR_OF_DAY,0);
        calendar.add(Calendar.MINUTE,0);
        calendar.add(Calendar.SECOND,0);
        calendar.add(Calendar.MILLISECOND,0);
        Date firstTime = calendar.getTime();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long durationInMillis = DAY_HOURS_IN_SECONDS * 1000;
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
                // TODO: 格式化为 html 格式, 并发送邮件
            }
        }, firstTime, DAY_HOURS_IN_SECONDS * 1000);
    }
}
