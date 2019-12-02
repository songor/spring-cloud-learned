package com.spring.cloud.example.ribbonconsumer.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.spring.cloud.example.ribbonconsumer.hystrix.User;
import org.springframework.web.client.RestTemplate;

/**
 * 创建请求命令 HystrixCommand
 * 同步执行：User user = new UserCommand(restTemplate, 1L).execute();
 * 异步执行：Future<User> futureUser = new UserCommand(restTemplate, 1L).queue();
 * 除了传统的同步执行与异步执行之外，我们还可以将 HystrixCommand 通过 Observable 来实现响应式执行方式。通过调用 observe() 和
 * toObservable() 方法可以返回 Observable 对象：
 * Observable<String> hotObservable = new UserCommand(restTemplate, 1L).observe();
 * Observable<String> coldObservable = new UserCommand(restTemplate, 1L).toObservable();
 */
public class UserCommand extends HystrixCommand {

    private RestTemplate restTemplate;

    private Long id;

    protected UserCommand(Setter setter, RestTemplate restTemplate, Long id) {
        super(setter);
        this.restTemplate = restTemplate;
        this.id = id;
    }

    @Override
    protected Object run() throws Exception {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
    }

    @Override
    protected Object getFallback() {
//        return super.getFallback();
        return new User();
    }

}
