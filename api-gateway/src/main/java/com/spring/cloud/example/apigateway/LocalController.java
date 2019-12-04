package com.spring.cloud.example.apigateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/local")
public class LocalController {

    @GetMapping("/hello")
    public String hello() {
        return "forward local hello";
    }

}
