package com.spring.cloud.example.rabbitmqhello;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Sender {

    @Autowired
    private AmqpTemplate template;

    public void send() {
        String context = "hello " + LocalDateTime.now();
        System.out.println("Sender: " + context);
        template.convertAndSend("hello", context);
    }

}
