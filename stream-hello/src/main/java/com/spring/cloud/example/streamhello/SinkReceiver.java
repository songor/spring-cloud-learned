package com.spring.cloud.example.streamhello;

import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * 注解 @EnableBinding 指定一个或多个定义了 @Input 或 @Output 注解的接口，以此实现对 Channel 的绑定。
 * 注解 @StreamListener 将被修饰的方法注册为消息中间件上数据流的事件监听器，注解中的属性值对应了监听的消息通道名。
 */

/**
 * Sink 和 Source 中分别通过 @Input 和 @Output 注解定义了输入通道和输出通道，
 * 而 Processor 通过继承 Source 和 Sink 的方式同时定义了一个输入通道和一个输出通道。
 * 这里的 @Input 和 @Output 注解都还有一个 value 属性，该属性可以用来设置消息通道的名称，
 * 这里 Sink 和 Source 中指定的消息通道名称分别为 input 和 output。
 */
@Log
@EnableBinding(value = {Sink.class, SinkSender.class})
public class SinkReceiver {

    /**
     * <b>详解 @StreamListener</b>
     * 消息转换：
     * 等价于 Spring Integration 中 @ServiceActivator + @Transformer
     * spring.cloud.stream.bindings.input.content-type=application/json
     * 在 Spring Cloud Stream 中实现了一套可扩展的消息转换机制，在消息消费逻辑执行之前，消息转换机制会根据消息头信息中声明的消息类型，
     * 找到对应的消息转换器并实现对消息的自动转换。
     * 消息反馈：
     * 通过 Processor + @SendTo 注解来指定返回内容的输出通道。
     */
    @StreamListener(Sink.INPUT)
    public void receive(Object payload) {
        log.info("Received: " + payload);
    }

}
