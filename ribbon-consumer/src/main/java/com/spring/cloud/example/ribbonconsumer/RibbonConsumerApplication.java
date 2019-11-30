package com.spring.cloud.example.ribbonconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * <b>服务消费者</b>
 * 获取服务：
 * 当我们启动服务消费者的时候，它会发送一个 REST 请求给服务注册中心，来获取上面注册的服务清单。
 * 获取服务是服务消费者的基础，所以必须确保 eureka.client.fetch-registry=true，该值默认为 true。若希望修改缓存清单的更新时间，
 * 可以通过 eureka.client.registry-fetch-interval-seconds=30 参数进行修改。
 * 服务调用：
 * 服务消费者在获取服务清单后，通过服务名可以获得具体提供服务的实例名和该实例的元数据信息。因为有这些服务实例的详细信息，所以客户端
 * 可以根据自己的需要决定具体调用哪个实例，在 Ribbon 中会默认采用轮询的方式进行调用，从而实现客户端的负载均衡。
 * 对于访问实例的选择，Eureka 中有 Region 和 Zone 的概念，一个 Region 中可以包含多个 Zone，每个服务客户端需要被注册到一个 Zone 中，
 * 所以每个客户端对应一个 Region 和一个 Zone。在进行服务调用的时候，优先访问同处一个 Zone 中的服务提供方，若访问不到，就访问其他的 Zone。
 * 服务下线：
 * 在客户端程序中，当服务实例进行正常的关闭操作时，它会触发一个服务下线的 REST 请求给 Eureka Server，告诉服务注册中心“我要下线了”。
 * 服务端在接收到请求之后，将该服务状态置为 DOWN，并把该下线事件广播出去。
 */

/**
 * 可以使用 Spring Cloud 应用中的 @SpringCloudApplication 注解来修饰应用主类
 */
@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
public class RibbonConsumerApplication {

    /**
     * 客户端负载均衡和服务端负载均衡最大的不同在于服务清单所存储的位置。在客户端负载均衡中，所有客户端节点都要维护着自己要访问的服务端清单，
     * 而这些服务端的清单来自于服务注册中心。
     * <b>Ribbon + Eureka</b>
     * ILoadBalancer:
     * ZoneAwareLoadBalancer
     * IPing:
     * NIWSDiscoveryPing
     * IRule:
     * ZoneAvoidanceRule 挑选出具体的服务实例
     * ServerList:
     * DiscoveryEnabledNIWSServerList 的 obtainServersViaDiscovery 方法依靠 EurekaClient 从服务注册中心获取到具体的服务实例 InstanceInfo 列表。
     * ServerListFilter:
     * ZonePreferenceServerListFilter 用于实现对服务实例列表的过滤
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(RibbonConsumerApplication.class, args);
    }

}
