package com.wenqi.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liangwenqi
 * @date 2023/3/27
 */
@RestController
@RequestMapping("hello")
public class HelloWorldController {
    @GetMapping("/world")
    public String helloWorld() {
        return "HelloWorld";
    }
}
