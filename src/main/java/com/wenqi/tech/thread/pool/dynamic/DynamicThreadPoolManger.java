package com.wenqi.tech.thread.pool.dynamic;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liangwenqi
 * @date 2023/4/27
 */
public class DynamicThreadPoolManger {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1));
        ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(1);

        scheduledThreadPool.scheduleAtFixedRate(() -> {
            System.out.println("活跃线程 -> getActiveCount:" + poolExecutor.getActiveCount());
            System.out.println("活跃线程 -> getCorePoolSize:" + poolExecutor.getCorePoolSize());
            System.out.println("活跃线程 -> getMaximumPoolSize:" + poolExecutor.getMaximumPoolSize());
            System.out.println("活跃线程 -> getPoolSize:" + poolExecutor.getPoolSize());
            System.out.println("活跃线程 -> getKeepAliveTime:" + poolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
            System.out.println("活跃线程 -> getQueue().size:" + poolExecutor.getQueue().size());
            System.out.println("############################## \n");
        }, 0, 500, TimeUnit.MILLISECONDS);

        for (int i = 0; i < 100; i++) {
            //for (int j = 0; j < 2; j++) {
            //    poolExecutor.execute(() -> {
            //        try {
            //            Thread.sleep(1000);
            //        } catch (InterruptedException e) {
            //            e.printStackTrace();
            //        }
            //    });
            //}
            poolExecutor.setCorePoolSize(10);
            poolExecutor.setMaximumPoolSize(20);
            poolExecutor.setKeepAliveTime(i++, TimeUnit.SECONDS);
            Thread.sleep(1000);
        }
    }
}
