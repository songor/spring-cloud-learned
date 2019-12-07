package com.spring.cloud.example.streamappsecond;

import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import java.util.Date;

@Log
@EnableBinding(value = {Processor.class})
public class AppSecond {

    @Bean
    @InboundChannelAdapter(value = Processor.OUTPUT, poller = @Poller(fixedDelay = "2000"))
    public MessageSource<Date> timerMessageSource() {
        return () -> new GenericMessage<>(new Date());
    }

    @StreamListener(Processor.INPUT)
    public void receive(Object payload) {
        log.info("Second App received: " + payload);
    }

}
