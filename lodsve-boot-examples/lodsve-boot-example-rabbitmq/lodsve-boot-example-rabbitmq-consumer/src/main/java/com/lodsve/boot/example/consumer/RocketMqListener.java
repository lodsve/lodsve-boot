/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
