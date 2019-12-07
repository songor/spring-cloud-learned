package com.spring.cloud.example.streamhello.integration;

import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;

/**
 * Spring Integration 原生支持
 */
@Log
@EnableBinding(value = {SinkIntegrationReceiver.SinkInput.class})
public class SinkIntegrationReceiver {

    @ServiceActivator(inputChannel = SinkInput.INPUT)
    public void receive(Object payload) {
        log.info("Received by integration: " + payload);
    }

    public interface SinkInput {

        String INPUT = "input-integration";

        @Input(SinkInput.INPUT)
        MessageChannel input();

    }

}
