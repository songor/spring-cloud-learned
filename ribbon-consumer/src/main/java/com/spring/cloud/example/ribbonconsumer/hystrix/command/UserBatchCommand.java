package com.spring.cloud.example.ribbonconsumer.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.spring.cloud.example.ribbonconsumer.hystrix.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class UserBatchCommand extends HystrixCommand<List<User>> {

    private RestTemplate restTemplate;

    private List<Long> ids;

    public UserBatchCommand(RestTemplate restTemplate, List<Long> ids) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserBatchGroup")));
        this.restTemplate = restTemplate;
        this.ids = ids;
    }

    @Override
    protected List<User> run() {
        return restTemplate.getForObject("http://USER-SERVICE/users?ids={1}", List.class, StringUtils.join(ids, ","));
    }

}
