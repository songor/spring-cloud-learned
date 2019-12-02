package com.spring.cloud.example.ribbonconsumer.hystrix.command;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.spring.cloud.example.ribbonconsumer.hystrix.User;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.Future;

@Log
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 同步执行
     *
     * @param id
     * @return
     */
    /**
     * 异常传播：
     * 除了 HystrixBadRequestException 之外，其他异常均会被 Hystrix 认为命令执行失败并触发服务降级的处理逻辑。通过设置 @HystrixCommand
     * 注解的 ignoreExceptions 参数，Hystrix 会将它包装在 HystrixBadRequestException 中抛出，这样就不会触发后续的 fallback 逻辑。
     */
    @HystrixCommand(fallbackMethod = "defaultUser", ignoreExceptions = {HystrixBadRequestException.class})
    public User getUserById(Long id) {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
    }

    /**
     * 异步执行
     *
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "defaultUser")
    public Future<User> getUserByIdAsync(Long id) {
        return new AsyncResult<User>() {
            @Override
            public User invoke() {
                return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
            }
        };
    }

    /**
     * HystrixObservableCommand
     * ObservableExecutionMode.EAGER 使用 observe() 执行方式
     * ObservableExecutionMode.LAZY 使用 toObservable() 执行方式
     *
     * @param id
     * @return
     */
    @HystrixCommand(observableExecutionMode = ObservableExecutionMode.EAGER, defaultFallback = "defaultUser")
    public Observable<User> getUserByIdObservable(Long id) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        User user = restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
                        subscriber.onNext(user);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @HystrixCommand(fallbackMethod = "stableUser")
    private User defaultUser() {
        // Assuming it is unstable
        return new User();
    }

    private User stableUser() {
        // Stable
        return new User();
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public User getUserByIdWithEx(Long id) {
        throw new RuntimeException("execute failed");
    }

    /**
     * 异常获取：
     * 在 fallback 实现方法的参数中增加 Throwable e 对象定义（必须是 Throwable，否则 FallbackDefinitionException）
     */
    private User fallback(Long id, Throwable e) {
        log.info("id: " + id);
        log.info("Catch exception: " + "execute failed".equals(e.getMessage()));
        return new User();
    }

}
