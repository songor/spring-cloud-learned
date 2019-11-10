package com.spring.cloud.example.helloservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 服务注册：
 * “服务提供者”在启动的时候会通过发送 REST 请求的方式将自己注册到 Eureka Server 上，同时带上了自身服务的一些元数据信息。
 * Eureka Server 接收到这个 REST 请求之后，将元数据信息存储在一个双层结构 Map 中，其中第一层的 key 是服务名，第二层的 key 是
 * 具体服务的实例名。
 * 在服务注册时，需要确认一下 eureka.client.register-with-eureka=true 参数是否正确，该值默认为 true。若设置为 false，将不会
 * 启动注册操作。
 * 服务同步：
 * 由于服务注册中心之间因互相注册为服务，当服务提供者发送注册请求到一个服务注册中心时，它会将该请求转发给集群中相连的其他注册中心，
 * 从而实现注册中心之间的服务同步。
 * 服务续约：
 * 在注册完服务之后，服务提供者会维护一个心跳用来持续告诉 Eureka Server “我还活着”，以防止 Eureka Server 的“剔除任务”将该
 * 服务实例从服务列表中排除出去。
 * eureka.instance.lease-renewal-interval-in-seconds=30 参数用于定义服务续约任务的调用间隔时间
 * eureka.instance.lease-expiration-duration-in-seconds=90 参数用于定义服务失效的时间
 */
@EnableEurekaClient
@SpringBootApplication
public class HelloServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloServiceApplication.class, args);
    }

}
