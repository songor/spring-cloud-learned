package com.spring.cloud.example.trace1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Trace ID 和 Span ID 是 Spring Cloud Sleuth 实现分布式服务跟踪的核心。在一次服务请求链路的调用过程中，会保持并传递同一个 Trace ID，
 * 从而将整个分布于不同微服务进程中的请求跟踪信息串联起来。
 * 对于每个 Span 来说，它必须有开始和结束两个节点，通过记录开始 Span 和结束 Span 的时间戳，就能统计出该 Span 的时间延迟。
 * <p>
 * spring-cloud-starter-sleuth 依赖会自动为当前应用构建起各通信通道的跟踪机制：
 * 通过诸如 RabbitMQ、Kafka（Spring Cloud Stream 绑定器）传递的请求
 * 通过 Zuul 代理传递的请求
 * 通过 RestTemplate 发起的请求
 * <p>
 * Sleuth 会在请求的 Header 中增加实现跟踪需要的重要信息：
 * X-B3-TraceId
 * X-B3-SpanId
 * X-B3-ParentSpanId
 * X-B3-Sampled：是否被抽样输出的标志，1 表示需要被输出，0 表示不需要被输出
 * X-Span-Name
 * <p>
 * 抽样收集：
 * Sleuth 中采用了抽样收集的方式来为跟踪信息打上收集标记，它代表了该信息是否要被后续的跟踪信息收集器获取和存储。
 * 由于跟踪日志信息数据的价值往往仅在最近的一段时间内非常有用，那么我们在设计抽样策略时，主要考虑在不对系统造成明显性能影响的情况下，
 * 以在日志保留时间窗内充分利用存储空间的原则来实现抽样策略。
 */
@RestController
@EnableEurekaClient
@SpringBootApplication
public class Trace1Application {

    private static final Logger LOG = LoggerFactory.getLogger(Trace1Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Trace1Application.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/trace-1")
    public String trace() {
        LOG.info("===trace-1===");
        return restTemplate().getForEntity("http://TRACE-2/trace-2", String.class).getBody();
    }

}
