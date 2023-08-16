package com.wenqi.springboot.controller.thread;

import com.wenqi.springboot.center.thread.transaction.MultiThreadTransaction;
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

    @GetMapping("/multiThreadInsert")
    public String multiThreadInsert(@RequestParam("id") String id) {
        multiThreadTransaction.multiThreadInsert(id);
        return "success";
    }
}
