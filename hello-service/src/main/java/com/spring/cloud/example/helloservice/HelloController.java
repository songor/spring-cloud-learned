package com.spring.cloud.example.helloservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/hello-param")
    public String helloWithParam(@RequestParam String name) {
        return "Hello " + name;
    }

    @GetMapping("/hello-header")
    public User helloWithHeader(@RequestHeader String name, @RequestHeader Integer age) {
        return new User(name, age);
    }

    @PostMapping("/hello-body")
    public String helloWithBody(@RequestBody User user) {
        return "Hello " + user.toString();
    }

}
