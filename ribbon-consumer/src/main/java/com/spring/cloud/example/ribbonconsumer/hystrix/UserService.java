package com.spring.cloud.example.ribbonconsumer.hystrix;

import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
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

    /**
     * @see HystrixPropertiesManager
     * @see HystrixCommandProperties
     * @see HystrixCollapserProperties
     * @see HystrixThreadPoolProperties
     * 优先级（由低到高）：
     * 全局默认值、全局配置属性（配置文件）、实例默认值、实例配置属性（配置文件）
     * <b>Command 属性</b>
     * 主要用来控制 HystrixCommand 命令的行为
     * (1) execution
     * 控制 HystrixCommand.run() 的执行
     * execution.isolation.strategy：隔离策略
     * execution.isolation.thread.timeoutInMilliseconds：超时时间
     * execution.timeout.enabled：是否启动超时时间
     * execution.isolation.thread.interruptOnTimeout：执行超时的时候是否要将它中断
     * execution.isolation.semaphore.maxConcurrentRequests：配置信号量的大小（并发请求数）
     * (2) fallback
     * 控制 HystrixCommand.getFallback() 的执行
     * fallback.isolation.semaphore.maxConcurrentRequests：最大并发请求数
     * fallback.enabled：服务降级策略是否启用
     * (3) circuitBreaker
     * 控制 HystrixCircuitBreaker 的行为
     * circuitBreaker.enabled：当服务请求命令失败时，是否使用断路器来跟踪其健康指标和熔断请求
     * circuitBreaker.requestVolumeThreshold：在滚动时间窗中，断路器熔断的最小请求数
     * circuitBreaker.sleepWindowInMilliseconds：断路器打开之后的休眠时间窗
     * circuitBreaker.errorThresholdPercentage：断路器打开的错误百分比条件，同时满足 circuitBreaker.requestVolumeThreshold
     * 和 circuitBreaker.errorThresholdPercentage，把断路器设置为“打开”状态
     * circuitBreaker.forceOpen：断路器强制进入“打开”状态
     * circuitBreaker.forceClosed：断路器强制进入“关闭”状态
     * (4) metrics
     * metrics.rollingStats.timeInMilliseconds：滚动时间窗的长度
     * metrics.rollingStats.numBuckets：滚动时间窗统计指标信息时划分“桶”的数量
     * metrics.rollingPercentile.enabled：对命令执行的延迟是否使用百分位数来跟踪和计算
     * metrics.rollingPercentile.timeInMilliseconds：百分位统计的滚动窗口的持续时间
     * metrics.rollingPercentile.numBuckets：百分位统计滚动窗口中使用“桶”的数量
     * metrics.rollingPercentile.bucketSize：在执行过程中每个“桶”中保留的最大执行次数
     * metrics.healthSnapshot.intervalInMilliseconds:采集影响断路器状态的健康快照的间隔等待时间
     * (5) requestContext
     * requestCache.enabled：是否开启请求缓存
     * requestLog.enabled：是否打印日志到 HystrixRequestLog 中
     * <b>Collapser 属性</b>
     * maxRequestsInBatch：一次请求合并批处理中允许的最大请求数
     * timerDelayInMilliseconds：批处理过程中每个命令延迟的时间
     * <b>ThreadPool 属性</b>
     * maxQueueSize：线程池的最大队列大小
     * coreSize：执行命令线程池的核心线程数
     * queueSizeRejectionThreshold：为队列设置拒绝阈值
     * metrics.rollingStats.numBuckets：滚动时间窗的长度
     * metrics.rollingStats.timeInMilliseconds：滚动时间窗统计指标信息时划分“桶”的数量
     */
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
            @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
            @HystrixProperty(name = "execution.isolation.thread.interruptOnTimeout", value = "true"),
            @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "10"),
            @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests", value = "10"),
            @HystrixProperty(name = "fallback.enabled", value = "true"),
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
            @HystrixProperty(name = "circuitBreaker.forceOpen", value = "false"),
            @HystrixProperty(name = "circuitBreaker.forceClosed", value = "false")
    }, threadPoolProperties = {
            @HystrixProperty(name = "maxQueueSize", value = "-1"),
            @HystrixProperty(name = "coreSize", value = "10"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "5"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "10"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
    })
    @HystrixCollapser(batchMethod = "batch", collapserProperties = {
            @HystrixProperty(name = "maxRequestsInBatch", value = "Integer.MAX_VALUE"),
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "10")
    })
    public void defaultProps() {
    }

}
