package com.spring.cloud.example.ribbonconsumer.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.spring.cloud.example.ribbonconsumer.hystrix.User;
import org.springframework.web.client.RestTemplate;

public class UserPostCommand extends HystrixCommand<User> {

    private RestTemplate restTemplate;

    private User user;

    private static final HystrixCommandKey COMMAND_KEY = HystrixCommandKey.Factory.asKey("postUser");

    public UserPostCommand(RestTemplate restTemplate, User user) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserGroup")).andCommandKey(COMMAND_KEY).
                andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("postUserThread")));
        this.restTemplate = restTemplate;
        this.user = user;
    }

    @Override
    protected User run() {
        User result = restTemplate.postForObject("http://USER-SERVICE/users", user, User.class);
        UserGetCommand.flushCache(result.getId());
        return result;
    }

}
