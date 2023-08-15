package com.wenqi.springboot.request.longpolling.demo01;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Wenqi Liang
 * @date 2023/6/23
 */
@EnableScheduling
@Slf4j
@RestController
@RequestMapping("/api")
public class BakeryController {

    private ExecutorService bakers = new ThreadPoolExecutor(5, 200, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    //@Autowired
    private ServletWebServerApplicationContext applicationContext;

    @GetMapping("/bake/{bakeGood}")
    public DeferredResult<String> publisher(@PathVariable("bakeGood") String bakedGood, @RequestParam("bakeTime") Integer bakeTime) {
        // 设置超时时间
        DeferredResult<String> output = new DeferredResult<>(50000L);
        output.onTimeout(() -> output.setErrorResult("the bakery is not responding in allowed time"));
        // 一定要异步执行, 否则占用大量的连接池, 阻塞controller的其他请求
        bakers.execute(() -> {
        try {
            Thread.sleep(bakeTime);
            output.setResult(String.format("Bake for %s complete and order dispatched. Enjoy!", bakedGood));
        } catch (Exception e) {
            output.setErrorResult("Something went wrong with your order!");
        }
        });
        return output;
    }


//    @Scheduled(cron = "*/1 * * * * ?")
    public void printTomcatInfo() {
        TomcatWebServer webServer = (TomcatWebServer) applicationContext.getWebServer();
        String tomcatInfo = webServer.getTomcat().getConnector().getProtocolHandler().getExecutor().toString();
        log.info("###### => tomcatInfo :{}", tomcatInfo);
    }
}
