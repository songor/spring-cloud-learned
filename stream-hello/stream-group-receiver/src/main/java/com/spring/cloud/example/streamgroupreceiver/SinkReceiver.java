package com.spring.cloud.example.streamgroupreceiver;

import lombok.extern.java.Log;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@Log
@EnableBinding(value = {Sink.class})
public class SinkReceiver {

    @StreamListener(Sink.INPUT)
    public void receive(Object payload){
        log.info("Group Receiver received: " + payload);
    }

}
