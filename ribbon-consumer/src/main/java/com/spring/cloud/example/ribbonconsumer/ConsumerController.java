package com.spring.cloud.example.ribbonconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/ribbon-consumer")
    public String helloConsumer() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
    }

    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/ribbon-consumer-enhanced")
    public String helloConsumerEnhanced() {
        return consumerService.helloService();
    }

    @GetMapping("/ribbon-consumer-time-out")
    public String helloConsumerTimeOut() {
        return consumerService.helloServiceTimeOut();
    }

}
