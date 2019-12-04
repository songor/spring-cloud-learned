package com.spring.cloud.example.feignconsumer;

//import feign.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.context.annotation.Bean;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class FeignConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }

    /**
     * 全局的日志级别
     */
    /**
     * Feign 的 Logger 级别主要有下面 4 类：
     * NONE：不记录任何信息
     * BASIC：仅记录请求方法、URL 以及响应状态码和执行时间
     * HEADERS：除了 BASIC 级别的信息之外，还会记录请求和响应的头信息
     * FULL：所有请求与响应的明细，包括头信息、请求体、元数据等
     */
//    @Bean
//    Logger.Level feignLoggerLevel() {
//        return Logger.Level.FULL;
//    }

}
