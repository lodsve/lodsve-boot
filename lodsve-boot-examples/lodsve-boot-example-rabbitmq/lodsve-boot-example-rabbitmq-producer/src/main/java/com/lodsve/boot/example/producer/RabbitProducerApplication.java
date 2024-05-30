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
package com.lodsve.boot.example.producer;

import com.lodsve.boot.example.pojo.MessageObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@SpringBootApplication
@RestController
public class RabbitProducerApplication {
    private final RabbitTemplate template;

    public RabbitProducerApplication(RabbitTemplate template) {
        this.template = template;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitProducerApplication.class, args);
    }

    @GetMapping("/")
    public void test() {
        template.convertAndSend("lodsve_boot_example_exchange1", "queue1.routing-key", "Hello World!");

        MessageObject message = new MessageObject();
        message.setId("id-1");
        message.setName("lodsve-boot-example");
        template.convertAndSend("lodsve_boot_example_exchange2", "queue2.routing-key", message);

        MessageObject message1 = new MessageObject();
        message1.setId("id-1");
        message1.setName("lodsve-boot-example1");
        MessageObject message2 = new MessageObject();
        message2.setId("id-2");
        message2.setName("lodsve-boot-example2");

        List<MessageObject> messages = new ArrayList<>(2);
        messages.add(message1);
        messages.add(message2);
        template.convertAndSend("lodsve_boot_example_exchange3", "queue3.routing-key", messages);

        template.convertAndSend("lodsve_boot_example_exchange4", "queue4.routing-key", new HashSet<>(messages));

        Map<String, MessageObject> map = new HashMap<>(2);
        map.put("message1", message1);
        map.put("message2", message2);
        template.convertAndSend("lodsve_boot_example_exchange5", "queue5.routing-key", map);
    }
}
