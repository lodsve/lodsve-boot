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
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Cluster;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Pool;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Sentinel;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Singleton;
import com.lodsve.boot.component.redis.dynamic.DynamicJedisConnectionFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * jedis多数据源.
 *
 * @author Hulk Sun
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
public class DynamicJedisConnectionConfiguration extends AbstractRedisConnectionConfiguration {

    /**
     * 构造函数.
     *
     * @param properties                    Redis 配置属性
     * @param sentinelConfigurationProvider 哨兵配置提供者
     * @param clusterConfigurationProvider  集群配置提供者
     */
    protected DynamicJedisConnectionConfiguration(ObjectProvider<RedisProperties> properties, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
    }

    /**
     * 创建 Redis 连接工厂 Bean.
     *
     * @param builderCustomizers Jedis 客户端配置构建器自定义器
     * @return RedisConnectionFactory
     * @throws UnknownHostException 当主机名无法解析时抛出
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    RedisConnectionFactory redisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) throws UnknownHostException {
        return createJedisConnectionFactory(builderCustomizers);
    }

    /**
     * 内部创建 Jedis 连接工厂的方法，支持多数据源动态切换.
     *
     * @param builderCustomizers Jedis 客户端配置构建器自定义器
     * @return RedisConnectionFactory (动态)
     */
    private RedisConnectionFactory createJedisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        Map<String, JedisConnectionFactory> factories = Maps.newHashMap();

        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(builderCustomizers);

        // 首先要判断创建多数据源
        if (isDynamicSentinelConnection(getProperties().getSentinels())) {
            // 多数据源 - 哨兵模式
            factories.putAll(createDynamicSentinelConnectionFactory(clientConfiguration));
        }

        if (isDynamicClusterConnection(getProperties().getClusters())) {
            // 多数据源 - 集群模式
            factories.putAll(createDynamicClusterConnectionFactory(clientConfiguration));
        }

        if (isDynamicSingletonConnection(getProperties().getSingletons())) {
            // 多数据源 - 单实例模式
            factories.putAll(createDynamicSingletonConnectionFactory(clientConfiguration));
        }

        RedisSentinelConfiguration sentinelConfiguration = getSentinelConfig(getProperties().getSentinel());
        if (sentinelConfiguration != null) {
            // 单数据源 - 哨兵模式
            factories.put("JedisConnectionFactory-Sentinel", new JedisConnectionFactory(sentinelConfiguration, clientConfiguration));
        }
        RedisClusterConfiguration clusterConfiguration = getClusterConfiguration(getProperties().getCluster());
        if (clusterConfiguration != null) {
            // 单数据源 - 集群模式
            factories.put("JedisConnectionFactory-Cluster", new JedisConnectionFactory(clusterConfiguration, clientConfiguration));
        }

        // 单数据源 - 单实例模式
        Singleton singleton = getProperties().getSingleton();
        if (singleton != null) {
            RedisStandaloneConfiguration configuration = getStandaloneConfig(singleton.getHost(), singleton.getPort(), singleton.getPassword(), singleton.getDatabase());
            if (null != configuration) {
                factories.put("JedisConnectionFactory-Standalone", new JedisConnectionFactory(configuration, clientConfiguration));
            }
        }

        AtomicReference<String> defaultName = new AtomicReference<>();
        AtomicInteger index = new AtomicInteger(0);
        factories.forEach((key, value) -> {
            if (0 == index.getAndIncrement()) {
                defaultName.set(key);
            }
            value.afterPropertiesSet();
        });

        return new DynamicJedisConnectionFactory(StringUtils.isEmpty(getProperties().getDefaultName()) ? defaultName.get() : getProperties().getDefaultName(), factories);
    }

    /**
     * 创建动态单机模式的连接工厂列表.
     *
     * @param clientConfiguration Jedis 客户端配置
     * @return 包含多个数据源名称及其对应连接工厂的 Map
     */
    private Map<String, JedisConnectionFactory> createDynamicSingletonConnectionFactory(JedisClientConfiguration clientConfiguration) {
        Map<String, Singleton> singletons = getProperties().getSingletons();
        Map<String, JedisConnectionFactory> factories = Maps.newHashMap();
        for (String name : singletons.keySet()) {
            Singleton singleton = singletons.get(name);
            RedisStandaloneConfiguration configuration = getStandaloneConfig(singleton.getHost(), singleton.getPort(), singleton.getPassword(), singleton.getDatabase());
            if (null == configuration) {
                continue;
            }
            JedisConnectionFactory factory = new JedisConnectionFactory(configuration, clientConfiguration);
            factories.put(name, factory);
        }

        return factories;
    }

    /**
     * 创建动态集群模式的连接工厂列表.
     *
     * @param clientConfiguration Jedis 客户端配置
     * @return 包含多个数据源名称及其对应连接工厂的 Map
     */
    private Map<String, JedisConnectionFactory> createDynamicClusterConnectionFactory(JedisClientConfiguration clientConfiguration) {
        Map<String, Cluster> clusters = getProperties().getClusters();
        Map<String, JedisConnectionFactory> factories = Maps.newHashMap();

        for (String name : clusters.keySet()) {
            RedisClusterConfiguration configuration = getClusterConfiguration(clusters.get(name));
            if (null == configuration) {
                continue;
            }
            JedisConnectionFactory factory = new JedisConnectionFactory(configuration, clientConfiguration);
            factories.put(name, factory);
        }

        return factories;
    }

    /**
     * 创建动态哨兵模式的连接工厂列表.
     *
     * @param clientConfiguration Jedis 客户端配置
     * @return 包含多个数据源名称及其对应连接工厂的 Map
     */
    private Map<String, JedisConnectionFactory> createDynamicSentinelConnectionFactory(JedisClientConfiguration clientConfiguration) {
        Map<String, Sentinel> sentinels = getProperties().getSentinels();
        Map<String, JedisConnectionFactory> factories = Maps.newHashMap();

        for (String name : sentinels.keySet()) {
            RedisSentinelConfiguration configuration = getSentinelConfig(sentinels.get(name));
            if (null == configuration) {
                continue;
            }

            JedisConnectionFactory factory = new JedisConnectionFactory(configuration, clientConfiguration);
            factories.put(name, factory);
        }

        return factories;
    }

    /**
     * 获取 Jedis 客户端配置.
     *
     * @param builderCustomizers 自定义器提供者
     * @return JedisClientConfiguration
     */
    private JedisClientConfiguration getJedisClientConfiguration(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        Pool pool = getProperties().getJedis().getPool();
        if (pool != null) {
            applyPooling(pool, builder);
        }
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    /**
     * 应用 Redis 属性到 Jedis 客户端配置构建器.
     *
     * @param builder 构建器
     * @return JedisClientConfigurationBuilder
     */
    private JedisClientConfigurationBuilder applyProperties(JedisClientConfigurationBuilder builder) {
        if (getProperties().isSsl()) {
            builder.useSsl();
        }
        if (getProperties().getTimeout() != null) {
            Duration timeout = getProperties().getTimeout();
            builder.readTimeout(timeout).connectTimeout(timeout);
        }
        if (StringUtils.hasText(getProperties().getClientName())) {
            builder.clientName(getProperties().getClientName());
        }
        return builder;
    }

    /**
     * 应用连接池配置.
     *
     * @param pool    连接池属性
     * @param builder 构建器
     */
    private void applyPooling(Pool pool, JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    /**
     * 将配置属性转换为 JedisPoolConfig.
     *
     * @param pool 连接池属性
     * @return JedisPoolConfig
     */
    private JedisPoolConfig jedisPoolConfig(Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMinIdle(pool.getMinIdle());
        config.setMaxIdle(pool.getMaxIdle());
        if (pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns().toMillis());
        }
        if (pool.getMaxWait() != null) {
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }
}
