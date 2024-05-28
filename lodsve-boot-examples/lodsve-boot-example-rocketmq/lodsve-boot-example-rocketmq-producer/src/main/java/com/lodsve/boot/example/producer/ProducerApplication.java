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

import com.lodsve.boot.LodsveBootApplication;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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
