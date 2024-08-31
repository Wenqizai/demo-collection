package com.wenqi.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liangwenqi
 * @date 2023/3/27
 */
@RestController
@RequestMapping("hello")
public class HelloWorldController {
    @GetMapping("/world")
    public String helloWorld(@RequestParam("id") Long id) {
        System.out.println(id);
        return "HelloWorld";
    }

    @GetMapping("/world2")
    public String helloWorld2(@RequestParam("id") Long id) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(id);
        return "HelloWorld2";
    }

    @PostMapping("/post/world")
    public String postHelloWorld(@RequestBody String id, HttpServletResponse response) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(id);
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        return "postHelloWorld";
    }
}
