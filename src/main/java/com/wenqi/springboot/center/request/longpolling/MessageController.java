package com.wenqi.springboot.center.request.longpolling;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liangwenqi
 * @date 2025/7/2
 */
@Slf4j
@RestController
@RequestMapping("/msg")
public class MessageController {

    private final ExecutorService executorService = new ThreadPoolExecutor(5, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @GetMapping("/get")
    public DeferredResult<String> getMsg() {
        DeferredResult<String> output = new DeferredResult<>(5000L);
        output.onTimeout(() -> output.setErrorResult("timeout"));
        executorService.execute(() -> {
            try {
                int interval = 10 * 1000 + RandomUtils.nextInt(0, 200);
                Thread.sleep(interval);
                output.setResult("receive a msg: " + UUID.randomUUID());
            } catch (Exception e) {
                output.setErrorResult("ex");
                Thread.currentThread().interrupt();
            }
        });
        return output;
    }

    /**
     * 方案1：收集固定时间窗口内的多条消息
     *
     * @param windowMs 时间窗口（毫秒），默认3000ms
     * @param maxCount 最大消息数量，默认5条
     * @return 多条消息的列表
     */
    @GetMapping("/getMultiMsg")
    public DeferredResult<List<String>> getMultiMsg(
            @RequestParam(defaultValue = "3000") long windowMs,
            @RequestParam(defaultValue = "5") int maxCount) {

        DeferredResult<List<String>> output = new DeferredResult<>(windowMs + 2000L);
        output.onTimeout(() -> {
            List<String> timeoutMsg = new ArrayList<>();
            timeoutMsg.add("Request timeout - no messages collected");
            output.setResult(timeoutMsg);
        });

        executorService.execute(() -> {
            List<String> messages = new ArrayList<>();
            long startTime = System.currentTimeMillis();

            try {
                log.info("开始收集消息，时间窗口: {}ms, 最大数量: {}", windowMs, maxCount);

                // 在时间窗口内收集消息
                while (System.currentTimeMillis() - startTime < windowMs && messages.size() < maxCount) {
                    // 模拟消息到达的随机间隔
                    int interval = 200 + RandomUtils.nextInt(0, 800);
                    Thread.sleep(interval);

                    // 模拟消息到达（这里可以替换为实际的消息队列逻辑）
                    if (RandomUtils.nextBoolean()) { // 50%概率有消息
                        String message = String.format("msg-%d: %s",
                                messages.size() + 1, UUID.randomUUID().toString().substring(0, 8));
                        messages.add(message);
                        log.info("收集到消息: {}", message);
                    }
                }

                // 如果没有收集到任何消息，返回空消息提示
                if (messages.isEmpty()) {
                    messages.add("No messages received in time window");
                }

                output.setResult(messages);
                log.info("返回 {} 条消息", messages.size());

            } catch (Exception e) {
                log.error("获取多条消息时发生错误", e);
                List<String> errorMsg = new ArrayList<>();
                errorMsg.add("Error collecting messages: " + e.getMessage());
                output.setResult(errorMsg);
                Thread.currentThread().interrupt();
            }
        });

        return output;
    }

    /**
     * 方案2：批量消息处理 - 收集到指定数量的消息后立即返回
     *
     * @param batchSize 批次大小，默认3条
     * @param timeoutMs 超时时间，默认5000ms
     * @return 批量消息列表
     */
    @GetMapping("/getBatchMsg")
    public DeferredResult<List<String>> getBatchMsg(
            @RequestParam(defaultValue = "3") int batchSize,
            @RequestParam(defaultValue = "5000") long timeoutMs) {

        DeferredResult<List<String>> output = new DeferredResult<>(timeoutMs);
        output.onTimeout(() -> {
            List<String> timeoutMsg = new ArrayList<>();
            timeoutMsg.add("Batch timeout - collected partial messages");
            output.setResult(timeoutMsg);
        });

        executorService.execute(() -> {
            List<String> messages = new ArrayList<>();

            try {
                log.info("开始批量收集消息，批次大小: {}, 超时时间: {}ms", batchSize, timeoutMs);

                // 收集到指定数量的消息后立即返回
                while (messages.size() < batchSize) {
                    // 模拟消息到达间隔
                    int interval = 300 + RandomUtils.nextInt(0, 700);
                    Thread.sleep(interval);

                    // 模拟消息到达
                    String message = String.format("batch-msg-%d: %s",
                            messages.size() + 1, UUID.randomUUID().toString().substring(0, 8));
                    messages.add(message);
                    log.info("批量收集到消息: {}", message);
                }

                output.setResult(messages);
                log.info("批量返回 {} 条消息", messages.size());

            } catch (Exception e) {
                log.error("批量获取消息时发生错误", e);
                List<String> errorMsg = new ArrayList<>();
                errorMsg.add("Error in batch collection: " + e.getMessage());
                output.setResult(errorMsg);
                Thread.currentThread().interrupt();
            }
        });

        return output;
    }

    /**
     * 方案3：Server-Sent Events (SSE) - 实时推送多条消息
     *
     * @param duration 推送持续时间（秒），默认10秒
     * @return SSE流
     */
    @GetMapping(value = "/getStreamMsg", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getStreamMsg(@RequestParam(defaultValue = "10") int duration) {

        SseEmitter emitter = new SseEmitter((long) duration * 1000 + 1000);

        executorService.execute(() -> {
            try {
                log.info("开始SSE消息推送，持续时间: {}秒", duration);

                long startTime = System.currentTimeMillis();
                int messageCount = 0;

                // 在指定时间内持续推送消息
                while (System.currentTimeMillis() - startTime < duration * 1000L) {
                    // 模拟消息到达间隔
                    int interval = 500 + RandomUtils.nextInt(0, 1000);
                    Thread.sleep(interval);

                    // 模拟消息到达
                    if (RandomUtils.nextInt(0, 100) < 70) { // 70%概率有消息
                        messageCount++;
                        String message = String.format("stream-msg-%d: %s at %d",
                                messageCount,
                                UUID.randomUUID().toString().substring(0, 8),
                                System.currentTimeMillis());

                        emitter.send(SseEmitter.event()
                                .name("message")
                                .data(message)
                                .id(String.valueOf(messageCount)));

                        log.info("SSE推送消息: {}", message);
                    }
                }

                // 发送完成事件
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("Stream completed. Total messages: " + messageCount));

                emitter.complete();
                log.info("SSE推送完成，总共推送 {} 条消息", messageCount);

            } catch (Exception e) {
                log.error("SSE推送时发生错误", e);
                emitter.completeWithError(e);
                Thread.currentThread().interrupt();
            }
        });

        return emitter;
    }
}
