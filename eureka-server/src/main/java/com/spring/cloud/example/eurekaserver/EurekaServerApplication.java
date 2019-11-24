package com.spring.cloud.example.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * <b>服务注册中心</b>
 * 失效剔除：
 * 有些时候，我们的服务实例并不一定会正常下线，可能由于内存溢出、网络故障等原因使得服务不能正常工作，而服务注册中心并未收到“服务下线”
 * 的请求。为了从服务列表中将这些无法提供服务的实例剔除，Eureka Server 在启动的时候会创建一个定时任务，默认每隔 60 秒将当前清单中超时（默认为 90 秒）
 * 没有续约的服务剔除出去。
 * 自我保护：
 * Eureka Server 在运行期间，会统计心跳失败的比例在 15 分钟之内是否低于 85%，如果出现低于的情况（单机调试），Eureka Server 会将当前的
 * 实例注册信息保护起来，让这些实例不会过期，尽可能保护这些注册信息。但是，在这段保护期内实例若出现问题，那么客户端很容易拿到实际
 * 已经不存在的服务实例，会出现调用失败的情况，所以客户端必须要有容错机制，比如可以使用请求重试、断路器等机制。
 * 由于本地调试很容易触发注册中心的保护机制，这会使得注册中心维护的服务实例不那么准确。所以，我们在本地进行开发的时候，可以使用
 * eureka.server.enable-self-preservation=false 参数来关闭保护机制，以确保注册中心将不可用的实例正确剔除。
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

}
