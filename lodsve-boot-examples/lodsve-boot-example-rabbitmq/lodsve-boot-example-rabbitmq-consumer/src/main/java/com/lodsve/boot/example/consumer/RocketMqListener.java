package com.lodsve.boot.example.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Component
public class RocketMqListener {

    @RabbitListener(queues = "queue1")
    public void listener1(String message) {
        System.out.println("queue1: " + message);
    }

    @RabbitListener(queues = "queue2")
    public void listener2(MessageObject2 message) {
        System.out.println("queue2: " + message);
        System.out.println("queue2: type: " + message.getClass().getName());
    }

    @RabbitListener(queues = "queue3")
    public void listener3(List<MessageObject2> message) {
        System.out.println("queue3: " + message);
        message.forEach(m -> System.out.println("queue3: generic type: " + m.getClass()));
    }

    @RabbitListener(queues = "queue4")
    public void listener4(Set<MessageObject2> message) {
        System.out.println("queue4: " + message);
        message.forEach(m -> System.out.println("queue4: generic type: " + m.getClass().getName()));
    }

    @RabbitListener(queues = "queue5")
    public void listener5(Map<String, MessageObject2> message) {
        System.out.println("queue5: " + message);
        message.forEach((k, v) -> System.out.printf("queue5: key type: %s, value type: %s!%n", k.getClass(), v.getClass()));
    }
}
