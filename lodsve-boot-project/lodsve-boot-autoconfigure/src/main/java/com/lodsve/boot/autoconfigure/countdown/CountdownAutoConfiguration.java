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
package com.lodsve.boot.autoconfigure.countdown;

import com.lodsve.boot.countdown.*;
import io.lettuce.core.RedisException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.util.List;

/**
 * Countdown配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnClass({RedisConnectionFactory.class, CountdownMessageListenerContainer.class})
@ConditionalOnProperty(name = "lodsve.countdown.enabled")
@EnableConfigurationProperties(CountdownProperties.class)
@Configuration
public class CountdownAutoConfiguration {
    private final RedisConnectionFactory connectionFactory;

    public CountdownAutoConfiguration(ObjectProvider<RedisConnectionFactory> objectProvider) {
        RedisConnectionFactory redisConnectionFactory = objectProvider.getIfUnique();
        if (null == redisConnectionFactory) {
            throw new RedisException("No any redis connection factory in the spring context!");
        }

        this.connectionFactory = redisConnectionFactory;
    }

    @Bean
    public RedisTemplate<Serializable, Serializable> countdownRedisTemplate() {
        RedisTemplate<Serializable, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public CountdownListener countdownListener(ApplicationEventPublisher applicationEventPublisher,
                                               CountdownEventResolver eventResolver) {
        return new CountdownListener(applicationEventPublisher, eventResolver);
    }

    @Bean
    public CountdownPublisher countdownPublisher(ApplicationEventPublisher applicationEventPublisher,
                                                 RedisTemplate<Serializable, Serializable> redisTemplate) {
        return new CountdownPublisher(redisTemplate, applicationEventPublisher);
    }

    @Bean
    public CountdownMessageListenerContainer countdownRedisMessageListenerContainer(CountdownListener countdownListener, CountdownProperties properties) {
        return new CountdownMessageListenerContainer(connectionFactory, countdownListener, properties.getDatabase());
    }

    @Bean
    public CountdownEventListener countdownEventListener(CountdownEventResolver eventResolver) {
        return new CountdownEventListener(eventResolver);
    }

    @Bean
    public CountdownEventResolver countdownEventResolver(ObjectProvider<List<CountdownEventHandler>> provider) {
        return new CountdownEventResolver(provider);
    }
}
