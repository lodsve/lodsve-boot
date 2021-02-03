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
package com.lodsve.boot.autoconfigure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lodsve.boot.redis.gson.GsonRedisSerializer;
import com.lodsve.boot.redis.jackson.Jackson2JsonObjectRedisSerializer;
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
 * @author sunhao(hulk)
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
