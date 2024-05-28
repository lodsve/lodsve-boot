/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
