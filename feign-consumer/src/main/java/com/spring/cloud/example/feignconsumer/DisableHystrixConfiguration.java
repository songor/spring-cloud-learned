package com.spring.cloud.example.feignconsumer;

import feign.Feign;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 针对某个服务客户端关闭 Hystrix 支持
 * 注意：会关闭全局 Hystrix 支持，需要注释 @Configuration
 */
//@Configuration
public class DisableHystrixConfiguration {

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

}
