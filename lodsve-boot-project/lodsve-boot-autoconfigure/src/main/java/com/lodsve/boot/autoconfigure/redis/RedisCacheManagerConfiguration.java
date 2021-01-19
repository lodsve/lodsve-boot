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
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
     * @param redisConnectionFactory redisConnectionFactory
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
