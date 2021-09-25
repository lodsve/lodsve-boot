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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lodsve.boot.component.redis.gson.GsonRedisSerializer;
import com.lodsve.boot.component.redis.jackson.Jackson2JsonObjectRedisSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * redis序列化配置.
 *
 * @author Hulk Sun
 */
@Configuration
public class RedisSerializerConfiguration {
    static final String PREFERRED_MAPPER_PROPERTY = "spring.mvc.converters.preferred-json-mapper";

    @Configuration
    @ConditionalOnClass(ObjectMapper.class)
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    @ConditionalOnProperty(name = PREFERRED_MAPPER_PROPERTY, havingValue = "jackson", matchIfMissing = true)
    public static class JacksonRedisSerializerConfiguration {
        @Bean
        public RedisSerializer<Object> serializer(ObjectProvider<ObjectMapper> objectProvider) {
            return new Jackson2JsonObjectRedisSerializer(objectProvider.getIfAvailable());
        }
    }

    @Configuration
    @ConditionalOnClass(Gson.class)
    @AutoConfigureAfter(GsonAutoConfiguration.class)
    @ConditionalOnProperty(name = PREFERRED_MAPPER_PROPERTY, havingValue = "gson")
    public static class GsonRedisSerializerConfiguration {
        @Bean
        public RedisSerializer<Object> serializer(ObjectProvider<Gson> gson) {
            return new GsonRedisSerializer<>(Object.class, gson.getIfAvailable());
        }
    }
}
