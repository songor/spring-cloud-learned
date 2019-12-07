package com.spring.cloud.example.streamhello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <b>绑定器</b>
 * Spring Cloud Stream 构建的应用程序与消息中间件之间是通过 Binder 相关联的，绑定器对于应用程序而言起到了隔离作用，
 * 它使得不同消息中间件的实现细节对应用程序来说是透明的。
 * 通过定义绑定器作为中间层，完美地实现了应用程序与消息中间件细节之间的隔离。通过向应用程序暴露统一的 Channel 通道，
 * 使得应用程序不需要再考虑各种不同的消息中间件的实现。
 * <b>发布-订阅模式</b>
 * Spring Cloud Stream 中的消息通信方式遵循了发布-订阅模式，当一条消息被投递到消息中间件后，它会通过共享的 Topic 主题进行广播，
 * 消息消费者在订阅的主题中收到它并触发自身的业务逻辑处理。
 * <b>消费组</b>
 * 如果在同一个主题上的应用需要启动多个实例的时候，我们可以通过 spring.cloud.stream.bindings.input.group 属性为一个应用指定一个组名，
 * 这样这个应用的多个实例在接收到消息的时候，只会有一个成员真正收到消息并进行处理。
 * <b>消费分区</b>
 * 当生产者将消息数据发送给多个消费者实例时，保证拥有共同特征的消息数据始终是由同一个消费者实例接收和处理。
 */
@RestController
@SpringBootApplication
public class StreamHelloApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamHelloApplication.class, args);
    }

    /**
     * 注入绑定接口
     */
    @Autowired
    private SinkSender sinkSender;

    @GetMapping("/sink-sender")
    public void sinkSender() {
        sinkSender.output().send(MessageBuilder.withPayload("Comes from SinkIntegrationSender").build());
    }

    /**
     * 注入消息通道
     * sinkSender.output() 方法实际获得的就是 SinkIntegrationSender 接口中定义的 MessageChannel 实例。
     * 要注意参数命名需要与通道同名才能被正确注入，或者也可以使用 @Qualifier 注解来特别指定具体实例的名称。
     */
    @Autowired
    private MessageChannel input;

    @GetMapping("/message-channel")
    public void messageChannel() {
        input.send(MessageBuilder.withPayload("Comes from MessageChannel").build());
    }

}
