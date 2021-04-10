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
