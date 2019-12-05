package com.spring.cloud.example.apigatewaydynamicfilter;

import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({DynamicFilterProperties.class})
@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class ApiGatewayDynamicFilterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayDynamicFilterApplication.class, args);
    }

    /**
     * 动态加载过滤器
     */
    @Bean
    public FilterLoader filterLoader(DynamicFilterProperties properties) throws Exception {
        FilterLoader loader = FilterLoader.getInstance();
        loader.setCompiler(new GroovyCompiler());
        FilterFileManager.setFilenameFilter(new GroovyFileFilter());
        FilterFileManager.init(properties.getInterval(), properties.getRoot() + "/pre", properties.getRoot() + "/post");
        return loader;
    }

}
