package com.spring.cloud.example.ribbonconsumer.hystrix.command;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.spring.cloud.example.ribbonconsumer.hystrix.User;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HystrixCollapser 抽象类指定了三个不同的类型：BatchReturnType, ResponseType, RequestArgumentType
 * 在资源有限并且短时间内会产生高并发请求的时候，为避免连接不够用而引起的延迟可以考虑使用请求合并器的方式来处理和优化
 * List<Future<User>> futures = new ArrayList<>();
 * for (int i = 0; i < 10; i++) {
 *     UserCollapserCommand command = new UserCollapserCommand(restTemplate, Long.valueOf(i));
 *     futures.add(command.queue());
 * }
 * for (Future<User> future : futures) {
 *     future.get();
 * }
 */
public class UserCollapserCommand extends HystrixCollapser<List<User>, User, Long> {

    private RestTemplate restTemplate;

    private Long id;

    public UserCollapserCommand(RestTemplate restTemplate, Long id) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("UserCollapser"))
                .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
        this.restTemplate = restTemplate;
        this.id = id;
    }

    /**
     * 定义获取请求参数的方法
     */
    @Override
    public Long getRequestArgument() {
        return id;
    }

    /**
     * 合并请求产生批量命令的具体实现方法
     */
    @Override
    protected HystrixCommand<List<User>> createCommand(Collection<CollapsedRequest<User, Long>> collapsedRequests) {
        List<Long> ids = new ArrayList<>(collapsedRequests.size());
        ids.addAll(collapsedRequests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
        return new UserBatchCommand(restTemplate, ids);
    }

    /**
     * 将批量结果拆分并传递给合并前的各个原子请求命令
     */
    @Override
    protected void mapResponseToRequests(List<User> batchResponse, Collection<CollapsedRequest<User, Long>> collapsedRequests) {
        int count = 0;
        for (CollapsedRequest<User, Long> collapsedRequest : collapsedRequests) {
            User user = batchResponse.get(count++);
            collapsedRequest.setResponse(user);
        }
    }

}
