package com.wenqi.designpattern.action.a2.storage;

import com.wenqi.designpattern.action.a2.dto.RequestInfo;

import java.util.List;
import java.util.Map;

/**
 * @author Wenqi Liang
 * @date 2022/10/16
 */
public class RedisMetricsStorage implements MetricsStorage {
    @Override
    public void saveRequestInfo(RequestInfo requestInfo) {

    }

    @Override
    public List<RequestInfo> getRequestInfos(String apiName, long startTimeInMillis, long endTimeInMillis) {
        return null;
    }

    @Override
    public Map<String, List<RequestInfo>> getRequestInfos(long startTimeInMillis, long endTimeInMillis) {
        return null;
    }
}
