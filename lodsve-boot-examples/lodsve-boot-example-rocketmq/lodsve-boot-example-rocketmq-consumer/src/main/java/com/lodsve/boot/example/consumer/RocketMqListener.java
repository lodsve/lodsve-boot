package com.lodsve.boot.example.consumer;

import com.lodsve.boot.autoconfigure.rocketmq.MessageHandler;
import com.lodsve.boot.autoconfigure.rocketmq.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@MessageHandler(topic = "LODSVE-BOOT-EXAMPLE-TOPIC")
@Component
public class RocketMqListener {
    public static final Logger logger = LoggerFactory.getLogger(RocketMqListener.class);

    @MessageListener(tag = "SIMPLE-TAG")
    public boolean listener1(String message) {
        System.out.println("SIMPLE-TAG: " + message);
        return true;
    }

    @MessageListener(tag = "MESSAGE-TAG")
    public boolean listener2(Message message) {
        System.out.println("MESSAGE-TAG: " + message);
        return true;
    }

    @MessageListener(tag = "LIST-TAG")
    public boolean listener3(List<Message> message) {
        System.out.println("LIST-TAG: " + message);
        Object obj = message.get(0);
        System.out.println("LIST-TAG: generic type: " + obj.getClass());
        return true;
    }

    @MessageListener(tag = "SET-TAG")
    public boolean listener4(Set<Message> message) {
        System.out.println("SET-TAG: " + message);
        message.forEach(m -> System.out.println("SET-TAG: generic type: " + m.getClass()));
        return true;
    }

    @MessageListener(tag = "MAP-TAG")
    public boolean listener5(Map<String, Message> message) {
        System.out.println("MAP-TAG: " + message);
        message.forEach((k, v) -> System.out.printf("MAP-TAG: key type: %s, value type: %s!%n", k.getClass(), v.getClass()));
        return true;
    }
}
