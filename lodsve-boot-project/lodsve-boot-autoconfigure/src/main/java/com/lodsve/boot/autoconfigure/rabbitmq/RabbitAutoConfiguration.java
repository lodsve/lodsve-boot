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
package com.lodsve.boot.autoconfigure.rabbitmq;

import com.lodsve.boot.autoconfigure.rabbitmq.binding.DirectQueueBinding;
import com.lodsve.boot.autoconfigure.rabbitmq.binding.FanoutQueueBinding;
import com.lodsve.boot.autoconfigure.rabbitmq.binding.TopicQueueBinding;
import com.lodsve.boot.autoconfigure.rabbitmq.converter.RabbitJackson2JsonMessageConverter;
import com.rabbitmq.client.Channel;
import org.apache.commons.collections.MapUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
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
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
    public MessageConverter messageConverter() {
        return new RabbitJackson2JsonMessageConverter();
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
