package com.spring.cloud.example.feignconsumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 通过 @FeignClient 注解指定服务名来绑定服务
 */
@FeignClient(value = "HELLO-SERVICE", fallback = HelloServiceFallback.class)
//@FeignClient(value = "HELLO-SERVICE", configuration = DisableHystrixConfiguration.class)
public interface HelloService {

    /**
     * 使用 Spring MVC 的注解来绑定具体该服务提供的 REST 接口
     */
    @GetMapping("/hello")
    String hello();

    @GetMapping("/hello-time-out")
    String helloTimeOut();

    @GetMapping("/hello-param")
    String helloWithParam(@RequestParam String name);

    @GetMapping("/hello-header")
    User helloWithHeader(@RequestHeader String name, @RequestHeader Integer age);

    @PostMapping("/hello-body")
    String helloWithBody(@RequestBody User user);

}
