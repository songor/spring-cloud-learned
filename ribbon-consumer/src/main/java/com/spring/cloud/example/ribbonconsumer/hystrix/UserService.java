package com.spring.cloud.example.ribbonconsumer.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

import java.util.List;
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
//    @CacheResult(cacheKeyMethod = "getUserByIdCacheKey")
    @CacheResult
    @HystrixCommand(fallbackMethod = "defaultUser", ignoreExceptions = {HystrixBadRequestException.class},
            commandKey = "getUserById", groupKey = "UserGroup", threadPoolKey = "getUserByIdThread")
    public User getUserById(@CacheKey("id") Long id) {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}", User.class, id);
    }

    /**
     * 使用 @CacheResult 注解的 cacheKeyMethod 方法来指定具体的生成函数；也可以通过使用 @CacheKey 注解在方法参数中指定用于组装缓存 Key 的元素
     */
    private Long getUserByIdCacheKey(Long id) {
        return id;
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

    /**
     * 使用 @CacheReomve 注解的 commandKey 属性来指明需要使用请求缓存的请求命令
     */
    @CacheRemove(commandKey = "getUserById")
    @HystrixCommand
    public void update(@CacheKey("id") User user) {
        restTemplate.postForObject("http://USER-SERVICE/users", user, User.class);
    }

    /**
     * 注解实现请求合并器
     * 虽然通过请求合并可以减少请求的数量以缓解依赖服务线程池的资源，但是在使用的时候也需要注意它所带来的额外开销，
     * 用于请求合并的延迟时间窗会使得依赖服务的请求延迟增高。
     * 是否使用请求合并器需要根据依赖服务调用的实际情况来选择，主要考虑：请求命令本身的延迟；延迟时间窗内的并发量
     */
    @HystrixCollapser(batchMethod = "findAll",
            collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds", value = "100")})
    public User find(Long id) {
        return null;
    }

    @HystrixCommand
    public List<User> findAll(List<Long> ids) {
        return restTemplate.getForObject("http://USER-SERVICE/users?ids={1}", List.class, StringUtils.join(ids, ","));
    }

}
