package com.wenqi.designpattern.action.a2;

import com.wenqi.designpattern.action.a2.dto.RequestInfo;
import com.wenqi.designpattern.action.a2.storage.MetricsStorage;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Wenqi Liang
 * @date 2022/10/16
 */
public class MetricsCollector {
    /**
     * 基于接口而非实现编程
     */
    private MetricsStorage metricsStorage;

    public MetricsCollector(MetricsStorage metricsStorage) {
        this.metricsStorage = metricsStorage;
    }

    public void  recordRequest(RequestInfo requestInfo) {
        if (requestInfo == null || StringUtils.isBlank(requestInfo.getApiName())) {
            return;
        }
        metricsStorage.saveRequestInfo(requestInfo);
    }
}
