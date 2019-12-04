package com.spring.cloud.example.feignconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/feign-consumer")
    public String helloConsumer() {
        return helloService.hello();
    }

    @GetMapping("/feign-consumer-enhanced")
    public String helloConsumerEnhanced() {
        StringBuilder sb = new StringBuilder();
        sb.append(helloService.helloWithParam("Feign")).append("\n");
        sb.append(helloService.helloWithHeader("Feign", 18)).append("\n");
        sb.append(helloService.helloWithBody(new User("Feign", 18)));
        return sb.toString();
    }

}
