package com.wenqi.springboot.request.longpolling.demo02;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collection;


/**
 * @author Wenqi Liang
 * @date 2023/6/23
 */
@Slf4j
@RestController
public class ApolloController {
    /**
     * guava中的MultiMap, 一个key可以保持多个value
     */
    private Multimap<String, DeferredResult<String>> watchRequest = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    /**
     * 模拟长轮询
     */
    @GetMapping("/watch/{namespace}")
    public DeferredResult<String> watch(@PathVariable("namespace") String namespace) {
        log.info("Request received");
        DeferredResult<String> deferredResult = new DeferredResult<>();
        // 当deferredResult完成时(不论是超时还是异常还是正常完成), 移除watchRequests中相应的watch key, 避免内存溢出
        deferredResult.onCompletion(() -> {
            System.out.println("remove key :" + namespace);
            watchRequest.remove(namespace, deferredResult);
        });
        watchRequest.put(namespace, deferredResult);
        log.info("Servlet thread released");
        return deferredResult;
    }

    /**
     * 模拟发布namespace配置
     */
    @GetMapping("/publish/{namespace}")
    public Object publishConfig(@PathVariable("namespace") String namespace) {
        if (watchRequest.containsKey(namespace)) {
            Collection<DeferredResult<String>> deferredResults = watchRequest.get(namespace);
            long time = System.currentTimeMillis();
            // 通知所有watch这个namespace变更的长轮询配置变更结果
            for (DeferredResult<String> deferredResult : deferredResults) {
                deferredResult.setResult(namespace + " changed: " + time);
            }
        }
        return "success";
    }
}
