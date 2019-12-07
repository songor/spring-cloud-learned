package com.spring.cloud.example.streamgroupsender;

import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

@Log
@EnableBinding(value = {Source.class})
public class SinkSender {

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "2000"))
    public MessageSource<String> firstMessageSource() {
        return () -> new GenericMessage<>("{\"id\":0,\"message\":\"Send to partition 0\"}");
    }

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "1000"))
    public MessageSource<String> secondMessageSource() {
        return () -> new GenericMessage<>("{\"id\":1,\"message\":\"Send to partition 1\"}");
    }

}
