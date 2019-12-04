package com.spring.cloud.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

/**
 * API 网关：
 * 它作为系统的统一入口，屏蔽了系统内部各个微服务的细节
 * 它可以与服务治理框架结合，实现自动化的服务实例维护以及负载均衡的路由转发
 * 它可以实现接口权限校验与微服务业务逻辑的解耦
 * 通过服务网关中的过滤器，在各生命周期中去校验请求的内容，将原本在对外服务层做的校验前移，保证了微服务的无状态性，
 * 同时降低了微服务的测试难度，让服务本身更集中关注业务逻辑的处理
 */
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public AccessTokenFilter accessTokenFilter() {
        return new AccessTokenFilter();
    }

    /**
     * 自定义路由映射规则
     * 为不同版本的微服务应用生成以版本号作为路由前缀定义的路由规则，通过这样具有版本号前缀的 URL 路径，
     * 我们就可以很容易地通过路径表达式来归类和管理这些具有版本信息的微服务了
     * 例如：helloservice-v1 -> /v1/helloservice/**
     */
    @Bean
    public PatternServiceRouteMapper patternServiceRouteMapper() {
        return new PatternServiceRouteMapper(
                "(?<name>^.+)-(?<version>v.+$)",
                "${version}/${name}");
    }

}
