package com.spring.cloud.example.apigatewaydynamicfilter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("zuul.filter")
public class DynamicFilterProperties {

    private String root;

    private Integer interval;

}
