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

import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Map;

/**
 * redis cache manager config.
 *
 * @author Hulk Sun
 */
@Configuration
public class RedisCacheManagerConfiguration extends CachingConfigurerSupport {
    private final RedisProperties redisProperties;

    public RedisCacheManagerConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * cacheManager 根据是否有自定义有效时间来动态修改
     *
     * @param redisConnectionFactory  redisConnectionFactory
     * @param redisCacheConfiguration redisCacheConfiguration
     * @param redisSerializer         redisSerializer
     * @return RedisCacheManager
     */
    @Bean
    public RedisCacheManager cacheManager(ObjectProvider<RedisConnectionFactory> redisConnectionFactory, RedisCacheConfiguration redisCacheConfiguration, ObjectProvider<RedisSerializer<Object>> redisSerializer) {
        Assert.notNull(redisConnectionFactory.getIfAvailable(), "redis connection factory must not be null!");
        Assert.notNull(redisSerializer.getIfAvailable(), "redis serializer must not be null!");

        Map<String, Long> keyTtl = redisProperties.getKeyTtl();
        if (MapUtils.isEmpty(keyTtl)) {
            return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory.getIfAvailable()), redisCacheConfiguration);
        } else {
            final Map<String, RedisCacheConfiguration> result = Maps.newHashMap();
            keyTtl.forEach((k, v) -> result.put(k, gsonRedisCacheConfigurationWithTtl(v, redisSerializer.getIfAvailable())));

            return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory.getIfAvailable()), redisCacheConfiguration, result);
        }
    }

    /**
     * 设置 redis 数据默认过期时间
     * 设置@cacheable 序列化方式
     *
     * @param redisSerializer redisSerializer
     * @return RedisCacheConfiguration
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectProvider<RedisSerializer<Object>> redisSerializer) {
        Assert.notNull(redisSerializer.getIfAvailable(), "redis serializer must not be null!");

        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer.getIfAvailable())).entryTtl(Duration.ofSeconds(redisProperties.getDefaultExpiration()));
        return configuration;
    }

    private RedisCacheConfiguration gsonRedisCacheConfigurationWithTtl(long seconds, RedisSerializer<Object> redisSerializer) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer)).entryTtl(Duration.ofSeconds(seconds));
        return configuration;
    }
}
