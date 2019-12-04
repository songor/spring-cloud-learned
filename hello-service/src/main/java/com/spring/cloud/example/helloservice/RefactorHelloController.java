package com.spring.cloud.example.helloservice;

import com.spring.cloud.example.helloserviceapi.dto.User;
import com.spring.cloud.example.helloserviceapi.service.IHelloService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefactorHelloController implements IHelloService {

    @Override
    public String hello() {
        return "Hello World";
    }

    @Override
    public String helloWithParam(String name) {
        return "Hello " + name;
    }

    @Override
    public User helloWithHeader(String name, Integer age) {
        return new User(name, age);
    }

    @Override
    public String helloWithBody(User user) {
        return "Hello " + user.toString();
    }

}
