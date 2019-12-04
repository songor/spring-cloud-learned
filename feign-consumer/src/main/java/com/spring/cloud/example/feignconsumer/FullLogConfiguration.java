package com.spring.cloud.example.feignconsumer;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 在具体的 Feign 客户端来指定配置类以实现是否要调整不同的日志级别
 */
@Configuration
public class FullLogConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
