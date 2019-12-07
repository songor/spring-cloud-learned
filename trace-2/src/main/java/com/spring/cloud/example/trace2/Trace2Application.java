package com.spring.cloud.example.trace2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@EnableEurekaClient
@SpringBootApplication
public class Trace2Application {

    private static final Logger LOG = LoggerFactory.getLogger(Trace2Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Trace2Application.class, args);
    }

    @GetMapping("/trace-2")
    public String trace(HttpServletRequest request) {
        LOG.info("===trace-2===");
        LOG.info("TraceId is {}, SpanId is {}, ParentSpanId is {}, Sampled is {}, SpanName is {}",
                request.getHeader("X-B3-TraceId"), request.getHeader("X-B3-SpanId"),
                request.getHeader("X-B3-ParentSpanId"), request.getHeader("X-B3-Sampled"),
                request.getHeader("X-Span-Name"));
        return "Trace";
    }

}
