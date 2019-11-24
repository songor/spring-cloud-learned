package com.spring.cloud.example.helloservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class HelloController {

//    @Autowired
//    private DiscoveryClient client;

    @GetMapping("/hello")
    public String hello() {
//        List<ServiceInstance> instances = client.getInstances("hello-service");
//        instances.stream().forEach((instance) -> log.info("/hello, host: " + instance.getHost() + ", port: " + instance.getPort() + ", service id: " + instance.getServiceId()));
        log.info("Received request");
        return "Hello World";
    }

}
