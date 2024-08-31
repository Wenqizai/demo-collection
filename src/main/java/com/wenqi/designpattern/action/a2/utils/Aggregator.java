package com.wenqi.designpattern.action.a2.utils;

import com.wenqi.designpattern.action.a2.dto.RequestInfo;
import com.wenqi.designpattern.action.a2.dto.RequestStat;

import java.util.List;

/**
 * @author Wenqi Liang
 * @date 2022/10/16
 */
public class Aggregator {
    public static RequestStat aggregate(List<RequestInfo> requestInfos, long durationMillis) {
        double maxRespTime = Double.MIN_VALUE;
        double minRespTime = Double.MAX_VALUE;
        double avgRespTime = -1;
        double p99RespTime = -1;
        double p999RespTime = -1;
        double sumRespTime = 0;
        long count = 0;
        for (RequestInfo requestInfo : requestInfos) {
            double responseTime = requestInfo.getResponseTime();
            if(maxRespTime < responseTime) {
                maxRespTime = responseTime;
            }
            if (minRespTime > responseTime) {
                minRespTime = responseTime;
            }
            sumRespTime += responseTime;
            count++;
        }
        if (count != 0) {
            avgRespTime = sumRespTime / count;
        }

        long tps = count / (durationMillis * 1000);
        // 响应时间从小到大排序
        requestInfos.sort((o1, o2) -> {
            double diff = o1.getResponseTime() - o2.getResponseTime();
            if (diff < 0.0) {
                return -1;
            } else if (diff > 0.0) {
                return 1;
            } else {
                return 0;
            }
        });

        int idx999 = (int)(count * 0.999);
        int idx99 = (int)(count * 0.99);

        if (count != 0) {
            p999RespTime = requestInfos.get(idx999).getResponseTime();
            p99RespTime = requestInfos.get(idx99).getResponseTime();
        }

        RequestStat requestStat = new RequestStat();
        requestStat.setMaxResponseTime(maxRespTime);
        requestStat.setMinResponseTime(minRespTime);
        requestStat.setAvgResponseTime(avgRespTime);
        requestStat.setP99ResponseTime(p99RespTime);
        requestStat.setP999ResponseTime(p999RespTime);
        requestStat.setCount(count);
        requestStat.setTps(tps);
        return requestStat;
    }
}
