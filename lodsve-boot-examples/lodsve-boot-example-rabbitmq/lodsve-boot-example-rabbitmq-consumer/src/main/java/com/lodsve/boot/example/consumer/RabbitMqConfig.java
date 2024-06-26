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

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
//@Configuration
public class RabbitMqConfig {
    @Bean
    public Queue queue1() {
        return new Queue("queue1");
    }

    @Bean
    public Queue queue2() {
        return new Queue("queue2");
    }

    @Bean
    public Queue queue3() {
        return new Queue("queue3");
    }

    @Bean
    public Queue queue4() {
        return new Queue("queue4");
    }

    @Bean
    public Queue queue5() {
        return new Queue("queue5");
    }

    @Bean
    public DirectExchange exchange1() {
        return new DirectExchange("lodsve_boot_example_exchange1");
    }

    @Bean
    public DirectExchange exchange2() {
        return new DirectExchange("lodsve_boot_example_exchange2");
    }

    @Bean
    public DirectExchange exchange3() {
        return new DirectExchange("lodsve_boot_example_exchange3");
    }

    @Bean
    public DirectExchange exchange4() {
        return new DirectExchange("lodsve_boot_example_exchange4");
    }

    @Bean
    public DirectExchange exchange5() {
        return new DirectExchange("lodsve_boot_example_exchange5");
    }

    @Bean
    public Binding binding1(Queue queue1, DirectExchange exchange1) {
        return BindingBuilder.bind(queue1).to(exchange1).with("queue1.routing-key");
    }

    @Bean
    public Binding binding2(Queue queue2, DirectExchange exchange2) {
        return BindingBuilder.bind(queue2).to(exchange2).with("queue2.routing-key");
    }

    @Bean
    public Binding binding3(Queue queue3, DirectExchange exchange3) {
        return BindingBuilder.bind(queue3).to(exchange3).with("queue3.routing-key");
    }

    @Bean
    public Binding binding4(Queue queue4, DirectExchange exchange4) {
        return BindingBuilder.bind(queue4).to(exchange4).with("queue4.routing-key");
    }

    @Bean
    public Binding binding5(Queue queue5, DirectExchange exchange5) {
        return BindingBuilder.bind(queue5).to(exchange5).with("queue5.routing-key");
    }

}
