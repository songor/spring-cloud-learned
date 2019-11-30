package com.spring.cloud.example.ribbonconsumer;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumerService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "helloServiceFallback")
    public String helloService() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
    }

    @HystrixCommand(fallbackMethod = "helloServiceFallback")
    public String helloServiceTimeOut() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello-time-out", String.class).getBody();
    }

    public String helloServiceFallback() {
        return "An error occurred";
    }

}
