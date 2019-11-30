package com.spring.cloud.example.ribbonconsumer;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumerService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * <b>工作流程：</b>
     * 1. 创建 HystrixCommand 或 HystrixObservableCommand 对象
     * 用来表示对依赖服务的操作请求，同时传递所有需要的参数。采用了命令模式来实现对服务调用操作的封装。
     * HystrixCommand 用在依赖的服务返回单个操作结果的时候
     * HystrixObservableCommand 用在依赖的服务返回多个操作结果的时候
     * 2、命令执行
     * HystrixCommand 实现了：
     * execute()：同步执行，从依赖的服务返回一个单一的结果对象，或是在发生错误的时候抛出异常。execute() 是通过 queue() 返回的
     * 异步对象 Future<R> 的 get() 方法来实现同步执行的。
     * queue():异步执行，直接返回一个 Future 对象，其中包含了服务执行结束时要返回的单一结果对象。该方法只是创建一个 Future 返回，
     * 并不会阻塞，这使得消费者可以自己决定如何处理异步操作。final Future<R> f = toObservable().toBlocking().toFuture();
     * HystrixObservableCommand 实现了：
     * observe()：返回 Observable 对象，它代表了操作的多个结果，它是一个 Hot Observable
     * toObservable()：Cold Observable
     * 3、结果是否被缓存
     * 4、断路器是否打开
     * 5、线程池、请求队列、信号量是否占满
     * Hystrix 所判断的线程池并非容器的线程池，而是每个依赖服务的专有线程池。Hystrix 为了保证不会因为某个依赖服务的问题影响到
     * 其他依赖服务而采用了“舱壁模式”来隔离每个依赖的服务。
     * 6、HystrixObservableCommand.construct() 或 HystrixCommand.run()
     * HystrixCommand.run()：返回一个 Observable，它发射单个结果并产生 onCompleted 的结束通知
     * HystrixObservableCommand.construct()：返回一个 Observable 对象
     * 7、计算断路器的健康度
     * Hystrix 会将“成功”、“失败”、“拒绝”、“超时”等信息报告给断路器，而断路器会维护一组计数器来统计这些数据。
     * 8、fallback 处理（服务降级）
     * 引起服务降级处理的情况有下面几种：
     * （1) 当前命令处于“熔断、短路”状态，断路器是打开的时候
     * （2）当前命令的线程池、请求队列、信号量被占满的时候
     * （3）HystrixObservableCommand.construct() 或 HystrixCommand.run() 抛出异常的时候
     * 在服务降级逻辑中，我们需要实现一个通用的响应结果，并且该结果的处理逻辑应当是从缓存或是根据一些静态逻辑来获取，
     * 而不是依赖网络请求获取。如果一旦要在降级逻辑中包含网络请求，那么该请求也必须被包装在 HystrixCommand 或是 HystrixObservableCommand
     * 中，从而形成级联的降级策略，而最终的降级逻辑一定不是一个依赖网络请求的处理，而是一个能够稳定地返回结果的处理逻辑。
     * 如果降级执行失败的时候：
     * execute()：抛出异常
     * queue()：正常返回 Future 对象，但是当调用 get() 来获取结果的时候会抛出异常
     * observe()：正常返回 Observable 对象，当订阅它的时候，将立即通过调用订阅者的 onError 方法来通知终止请求
     * toObservable()：同 observe()
     * 9、返回成功的响应
     */
    @HystrixCommand(fallbackMethod = "helloServiceFallback")
    public String helloService() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
    }

    @HystrixCommand(fallbackMethod = "helloServiceFallback")
    public String helloServiceTimeOut() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello-time-out", String.class).getBody();
    }

    public String helloServiceFallback() {
        return "An error occurred";
    }

}
