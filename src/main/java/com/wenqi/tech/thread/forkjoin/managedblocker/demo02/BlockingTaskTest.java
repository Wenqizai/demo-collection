package com.wenqi.tech.thread.forkjoin.managedblocker.demo02;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Wenqi Liang
 * @date 2022/10/7
 */
public class BlockingTaskTest {
//    public static void main(String[] args) {
//        final List<String> pagesHtml = Stream
//                .of("https://google.com", "https://stackoverflow.com", "...")
//                .map((url) -> BlockingTasks.callInManagedBlock(() -> {
//                    // doSomething ...
//                    sleep();
//                    return url;
//                }))
//                .collect(Collectors.toList());
//    }


    public static void main(String[] args) {
        System.out.println(DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now()) + ": Start");
        IntStream.range(0, 64).parallel().forEach((x) -> sleep());
        System.out.println(DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now()) + ": End");
    }

    public static void sleep() {
        try {
            System.out.println(DateTimeFormatter.ISO_LOCAL_TIME.format(LocalTime.now()) + ": Sleeping " + Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }
}
