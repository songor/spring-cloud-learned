package com.spring.cloud.example.feignconsumer;

import org.springframework.stereotype.Component;

/**
 * 服务降级
 */
@Component
public class HelloServiceFallback implements HelloService {

    @Override
    public String hello() {
        return "An error occurred";
    }

    @Override
    public String helloTimeOut() {
        return "An error occurred";
    }

    @Override
    public String helloWithParam(String name) {
        return "An error occurred";
    }

    @Override
    public User helloWithHeader(String name, Integer age) {
        return new User("Invalid", 0);
    }

    @Override
    public String helloWithBody(User user) {
        return "An error occurred";
    }

}
