package com.neo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    static final Logger log = LoggerFactory.getLogger(HelloWorldController.class);

    @RequestMapping("/hello")
    public String index() {
        log.info("收到请求，执行线程是：{}", Thread.currentThread());
        return "Hello World";
    }

    @GetMapping("/foo")
    public ResponseEntity<String> foo() {
        // 返回字符串 “controller”
        return ResponseEntity.ok("controller");
    }
}