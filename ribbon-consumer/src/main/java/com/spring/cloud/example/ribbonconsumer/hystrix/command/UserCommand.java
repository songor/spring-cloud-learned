package com.spring.cloud.example.ribbonconsumer.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.spring.cloud.example.ribbonconsumer.hystrix.User;
import lombok.extern.java.Log;
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
@Log
public class UserCommand extends HystrixCommand {

    private RestTemplate restTemplate;

    private Long id;

    /**
     * 通过设置命令组，Hystrix 会根据组来组织和统计命令的告警、仪表盘等信息。
     * 默认情况下，Hystrix 会让相同组名的命令使用同一个线程池，所以我们需要在创建 Hystrix 命令时为其指定命令组名来实现默认的线程池划分。
     * Hystrix 还提供了 HystrixThreadPoolKey 来对线程池进行设置，通过它我们可以实现更细粒度的线程池划分。尽量通过 HystrixThreadPoolKey
     * 的方式来指定线程池的划分，而不是通过组名的默认方式实现划分，因为多个不同的命令可能从业务逻辑上来看属于同一个组，
     * 但是往往从实现本身上需要跟其他命令进行隔离。
     */
    public UserCommand(RestTemplate restTemplate, Long id) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserGroup")).
                andCommandKey(HystrixCommandKey.Factory.asKey("getUserById")).
                andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("getUserByIdThread")));
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
        // 异常获取
        Throwable throwable = getExecutionException();
        log.info(throwable.getMessage());
        return new User();
    }

}
