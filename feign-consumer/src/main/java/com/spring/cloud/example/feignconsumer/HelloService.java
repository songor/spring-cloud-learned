package com.spring.cloud.example.feignconsumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 通过 @FeignClient 注解指定服务名来绑定服务
 */
@FeignClient("HELLO-SERVICE")
public interface HelloService {

    /**
     * 使用 Spring MVC 的注解来绑定具体该服务提供的 REST 接口
     */
    @GetMapping("/hello")
    String hello();

}
