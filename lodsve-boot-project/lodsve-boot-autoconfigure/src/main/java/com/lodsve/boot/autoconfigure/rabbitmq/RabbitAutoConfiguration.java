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
package com.lodsve.boot.autoconfigure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lodsve.boot.autoconfigure.rabbitmq.binding.DirectQueueBinding;
import com.lodsve.boot.autoconfigure.rabbitmq.binding.FanoutQueueBinding;
import com.lodsve.boot.autoconfigure.rabbitmq.binding.TopicQueueBinding;
import com.rabbitmq.client.Channel;
import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ Auto Configuration.
 *
 * @author Hulk Sun
 */
@Configuration
@AutoConfigureAfter(org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class)
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitAutoConfiguration implements ApplicationContextAware {
    private final RabbitProperties rabbitProperties;
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>(16);

    public RabbitAutoConfiguration(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    @Bean
    public MessageConverter messageConverter(ObjectProvider<ObjectMapper> objectMapperProvider) {
        ObjectMapper mapper = objectMapperProvider.getIfUnique(ObjectMapper::new);
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);
        converter.setAlwaysConvertToInferredType(true);

        return converter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        buildBeans();
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        beanDefinitionMap.forEach(beanFactory::registerBeanDefinition);
    }

    private void buildBeans() {
        Map<String, QueueConfig> queues = rabbitProperties.getQueues();

        if (MapUtils.isEmpty(queues)) {
            return;
        }

        for (String queueName : queues.keySet()) {
            QueueConfig config = queues.get(queueName);

            // 1. 创建queue
            createQueue(queueName, config.isDurable(), config.isExclusive(), config.isAutoDelete());
            // 2. 创建binding
            createBinding(queueName, config);
        }
    }

    private void createBinding(String queueName, QueueConfig config) {
        ExchangeType type = config.getExchangeType();
        Class<? extends Binding> bindingRawClass;
        Class<? extends Exchange> exchangeRawClass;
        switch (type) {
            case TOPIC:
                bindingRawClass = TopicQueueBinding.class;
                exchangeRawClass = TopicExchange.class;
                break;
            case DIRECT:
                bindingRawClass = DirectQueueBinding.class;
                exchangeRawClass = DirectExchange.class;
                break;
            case FANOUT:
                bindingRawClass = FanoutQueueBinding.class;
                exchangeRawClass = FanoutExchange.class;
                break;
            case HEADERS:
                throw new RuntimeException("Lodsve-Boot not support HeadersExchange now!");
            default:
                bindingRawClass = null;
                exchangeRawClass = null;
        }

        BeanDefinitionBuilder binding = BeanDefinitionBuilder.genericBeanDefinition(bindingRawClass);
        binding.addConstructorArgReference(config.getExchangeName());
        binding.addConstructorArgReference(queueName);
        if (ExchangeType.FANOUT != type) {
            binding.addConstructorArgValue(config.getRoutingKey());
        }

        beanDefinitionMap.put(config.getExchangeName() + "_queue_binding", binding.getBeanDefinition());

        // 3. 创建exchange
        if (!beanDefinitionMap.containsKey(config.getExchangeName())) {
            BeanDefinitionBuilder exchange = BeanDefinitionBuilder.genericBeanDefinition(exchangeRawClass);
            exchange.addConstructorArgValue(config.getExchangeName());
            beanDefinitionMap.put(config.getExchangeName(), exchange.getBeanDefinition());
        }

    }

    private void createQueue(String queueName, boolean durable, boolean exclusive, boolean autoDelete) {
        BeanDefinitionBuilder queue = BeanDefinitionBuilder.genericBeanDefinition(Queue.class);
        queue.addConstructorArgValue(queueName);
        queue.addConstructorArgValue(durable);
        queue.addConstructorArgValue(exclusive);
        queue.addConstructorArgValue(autoDelete);

        beanDefinitionMap.put(queueName, queue.getBeanDefinition());
    }
}
