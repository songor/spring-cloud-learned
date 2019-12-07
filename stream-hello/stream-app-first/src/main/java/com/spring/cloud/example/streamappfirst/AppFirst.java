package com.spring.cloud.example.streamappfirst;

import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.SendTo;

/**
 * 消息反馈
 */
@Log
@EnableBinding(value = {Processor.class})
public class AppFirst {

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public Object receive(Object payload) {
        log.info("First App received: " + payload);
        return "Custom message - " + payload;
    }

    @ServiceActivator(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public Object receiveByIntegration(Object payload) {
        log.info("First App received by integration: " + payload);
        return "Custom message by integration - " + payload;
    }

}
