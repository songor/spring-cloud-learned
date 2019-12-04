package com.spring.cloud.example.feignconsumer;

import com.spring.cloud.example.helloserviceapi.service.IHelloService;
//import org.springframework.cloud.openfeign.FeignClient;

//@FeignClient("HELLO-SERVICE")
public interface RefactorHelloService extends IHelloService {
}
