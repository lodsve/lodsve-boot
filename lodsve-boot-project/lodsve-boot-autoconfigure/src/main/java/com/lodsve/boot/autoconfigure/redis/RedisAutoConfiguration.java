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
package com.lodsve.boot.autoconfigure.redis;

import com.lodsve.boot.component.redis.dynamic.DynamicLettuceConnectionFactory;
import com.lodsve.boot.component.redis.dynamic.DynamicRedisConnectionFactoryAspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.net.UnknownHostException;

/**
 * Redis配置.
 *
 * @author sunhao(hulk)
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass({RedisOperations.class, DynamicLettuceConnectionFactory.class})
@Import({DynamicLettuceConnectionConfiguration.class, DynamicJedisConnectionConfiguration.class, RedisSerializerConfiguration.class, RedisCacheManagerConfiguration.class})
public class RedisAutoConfiguration {
    /**
     * 重写Redis序列化方式，使用Json方式:
     * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的。RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer。
     * Spring Data JPA为我们提供了下面的Serializer：
     * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer。
     * 在此我们将自己配置RedisTemplate并定义Serializer。
     *
     * @param redisConnectionFactory redisConnectionFactory
     * @param redisSerializer        redisSerializer
     * @return RedisTemplate&lt;String, Object&gt;
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(ObjectProvider<RedisConnectionFactory> redisConnectionFactory, ObjectProvider<RedisSerializer<Object>> redisSerializer) {
        Assert.notNull(redisConnectionFactory.getIfAvailable(), "Redis connection factory must not null!");
        Assert.notNull(redisSerializer.getIfAvailable(), "redis serializer must not be null!");

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory.getIfAvailable());

        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置值（value）的序列化
        redisTemplate.setValueSerializer(redisSerializer.getIfAvailable());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(redisSerializer.getIfAvailable());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public DynamicRedisConnectionFactoryAspect aspect() {
        return new DynamicRedisConnectionFactoryAspect();
    }
}
