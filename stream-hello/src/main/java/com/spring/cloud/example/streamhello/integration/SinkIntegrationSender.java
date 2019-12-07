package com.spring.cloud.example.streamhello.integration;

import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Spring Integration 原生支持
 */
@Log
@EnableBinding(value = {SinkIntegrationSender.SinkOutput.class})
public class SinkIntegrationSender {

    @Bean
    @InboundChannelAdapter(value = SinkOutput.OUTPUT, poller = @Poller(fixedDelay = "1000"))
    public MessageSource<Date> timerMessageSource() {
        return () -> new GenericMessage<>(new Date());
    }

    /**
     * 消息转换
     */
    @Transformer(inputChannel = SinkOutput.OUTPUT, outputChannel = SinkOutput.OUTPUT)
    public Object transform(Date message) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(message);
    }

    public interface SinkOutput {

        String OUTPUT = "input-integration";

        @Output(SinkOutput.OUTPUT)
        MessageChannel output();

    }

}
