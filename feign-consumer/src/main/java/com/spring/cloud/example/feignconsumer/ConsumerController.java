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

    @GetMapping("/feign-consumer-time-out")
    public String helloConsumerTimeOut() {
        return helloService.helloTimeOut();
    }

    @GetMapping("/feign-consumer-enhanced")
    public String helloConsumerEnhanced() {
        StringBuilder sb = new StringBuilder();
        sb.append(helloService.helloWithParam("Feign")).append("\n");
        sb.append(helloService.helloWithHeader("Feign", 18)).append("\n");
        sb.append(helloService.helloWithBody(new User("Feign", 18)));
        return sb.toString();
    }

    /**
     * The bean 'HELLO-SERVICE.FeignClientSpecification' could not be registered.
     * A bean with that name has already been defined and overriding is disabled.
     */

//    @Autowired
//    private RefactorHelloService refactorHelloService;

//    @GetMapping("/feign-consumer-inheritance")
//    public String helloConsumerInheritance() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(refactorHelloService.helloWithParam("Feign")).append("\n");
//        sb.append(refactorHelloService.helloWithHeader("Feign", 18)).append("\n");
//        sb.append(refactorHelloService.helloWithBody(new com.spring.cloud.example.helloserviceapi.dto.User("Feign", 18)));
//        return sb.toString();
//    }

}
