package com.spring.cloud.example.ribbonconsumer.hystrix.command;

import com.netflix.hystrix.HystrixObservableCommand;
import com.spring.cloud.example.ribbonconsumer.hystrix.User;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

/**
 * HystrixObservableCommand 获取能发射多次的 Observable
 */
public class UserObservableCommand extends HystrixObservableCommand {

    private RestTemplate restTemplate;

    private Long id;

    protected UserObservableCommand(Setter setter, RestTemplate restTemplate, Long id) {
        super(setter);
        this.restTemplate = restTemplate;
        this.id = id;
    }

    @Override
    protected Observable<User> construct() {
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

    @Override
    protected Observable resumeWithFallback() {
        return super.resumeWithFallback();
    }

}
