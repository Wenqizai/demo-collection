package com.wenqi.springboot.controller.thread;

import com.wenqi.springboot.center.thread.transaction.MultiThreadTransaction;
import com.wenqi.springboot.center.thread.transaction.MultiUpdateDeadLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * @author liangwenqi
 * @date 2023/8/16
 */
@RestController
@RequestMapping("thread")
public class ThreadController {

    @Autowired
    private MultiThreadTransaction multiThreadTransaction;
    @Autowired
    private MultiUpdateDeadLock multiUpdateDeadLock;

    @GetMapping("/multiThreadInsert")
    public String multiThreadInsert(@RequestParam("id") String id) {
        multiThreadTransaction.multiThreadInsertGraceful(id);
        return "success";
    }


    @GetMapping("/testUpdateDeadLock")
    public String testUpdateDeadLock() {
        multiUpdateDeadLock.testUpdateDeadLock();
        return "success";
    }


}
