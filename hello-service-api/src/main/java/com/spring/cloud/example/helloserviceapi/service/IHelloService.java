package com.spring.cloud.example.helloserviceapi.service;

import com.spring.cloud.example.helloserviceapi.dto.User;
import org.springframework.web.bind.annotation.*;

/**
 * 服务提供者：
 * （1）增加 hello-service-api 依赖
 * （2）创建 RefactorHelloController implements IHelloService
 * （3）Override 接口
 * 服务消费者：
 * （1）增加 hello-service-api 依赖
 * （2）创建 RefactorHelloService extends IHelloService，然后添加 @FegionClient(value = "HELLO-SERVICE") 注解来绑定服务
 */
@RequestMapping("/refactor")
public interface IHelloService {

    @GetMapping("/hello")
    String hello();

    @GetMapping("/hello-param")
    String helloWithParam(@RequestParam("name") String name);

    @GetMapping("/hello-header")
    User helloWithHeader(@RequestHeader("name") String name, @RequestHeader("age") Integer age);

    @PostMapping("/hello-body")
    String helloWithBody(@RequestBody User user);

}
