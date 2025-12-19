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

import com.lodsve.boot.LodsveBootApplication;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@SpringBootApplication
@RestController
public class ProducerApplication {
    private final RocketMQTemplate template;

    public ProducerApplication(RocketMQTemplate template) {
        this.template = template;
    }

    public static void main(String[] args) {
        LodsveBootApplication.run(ProducerApplication.class, args);
    }

    @GetMapping("/")
    public void test() {
        template.syncSend("LODSVE-BOOT-EXAMPLE-TOPIC:SIMPLE-TAG", "Hello World!");

        Message message = new Message();
        message.setId("id-1");
        message.setName("lodsve-boot-example");
        template.syncSend("LODSVE-BOOT-EXAMPLE-TOPIC:MESSAGE-TAG", message);

        Message message1 = new Message();
        message1.setId("id-1");
        message1.setName("lodsve-boot-example1");
        Message message2 = new Message();
        message2.setId("id-2");
        message2.setName("lodsve-boot-example2");

        List<Message> messages = new ArrayList<>(2);
        messages.add(message1);
        messages.add(message2);
        template.syncSend("LODSVE-BOOT-EXAMPLE-TOPIC:LIST-TAG", messages);

        template.syncSend("LODSVE-BOOT-EXAMPLE-TOPIC:SET-TAG", new HashSet<>(messages));

        Map<String, Message> map = new HashMap<>(2);
        map.put("message1", message1);
        map.put("message2", message2);
        template.syncSend("LODSVE-BOOT-EXAMPLE-TOPIC:MAP-TAG", map);
    }
}
