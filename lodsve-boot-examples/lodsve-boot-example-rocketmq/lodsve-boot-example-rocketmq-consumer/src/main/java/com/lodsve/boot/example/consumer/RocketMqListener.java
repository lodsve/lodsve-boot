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
