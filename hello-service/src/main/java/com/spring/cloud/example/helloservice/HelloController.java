package com.spring.cloud.example.helloservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        log.info("Received request");
        return "Hello World";
    }

    @GetMapping("/hello-time-out")
    public String helloTimeOut() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

}
