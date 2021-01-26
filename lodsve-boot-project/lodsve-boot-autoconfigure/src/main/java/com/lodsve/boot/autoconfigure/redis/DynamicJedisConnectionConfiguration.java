/*
 * Copyright (C) 2019, All rights Reserved, Designed By Technology Middle Platform
 * @Copyright: 2020 www.oppo.com Inc. All rights reserved.
 * 注意：本内容仅限于OPPO公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.lodsve.boot.autoconfigure.redis;

import com.google.common.collect.Maps;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Cluster;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Pool;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Sentinel;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Singleton;
import com.lodsve.boot.redis.dynamic.DynamicJedisConnectionFactory;
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
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
public class DynamicJedisConnectionConfiguration extends AbstractRedisConnectionConfiguration {

    protected DynamicJedisConnectionConfiguration(ObjectProvider<RedisProperties> properties, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    RedisConnectionFactory redisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) throws UnknownHostException {
        return createJedisConnectionFactory(builderCustomizers);
    }

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
        RedisStandaloneConfiguration configuration = getStandaloneConfig(getProperties().getUrl(), getProperties().getHost(), getProperties().getPort(), getProperties().getPassword(), getProperties().getDatabase());
        if (null != configuration) {
            factories.put("JedisConnectionFactory-Standalone", new JedisConnectionFactory(configuration, clientConfiguration));
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

    private Map<String, JedisConnectionFactory> createDynamicSingletonConnectionFactory(JedisClientConfiguration clientConfiguration) {
        Map<String, Singleton> singletons = getProperties().getSingletons();
        Map<String, JedisConnectionFactory> factories = Maps.newHashMap();
        for (String name : singletons.keySet()) {
            Singleton singleton = singletons.get(name);
            RedisStandaloneConfiguration configuration = getStandaloneConfig("", singleton.getHost(), singleton.getPort(), singleton.getPassword(), singleton.getDatabase());
            if (null == configuration) {
                continue;
            }
            JedisConnectionFactory factory = new JedisConnectionFactory(configuration, clientConfiguration);
            factories.put(name, factory);
        }

        return factories;
    }

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

    private JedisClientConfiguration getJedisClientConfiguration(
        ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        Pool pool = getProperties().getJedis().getPool();
        if (pool != null) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

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

    private void applyPooling(Pool pool, JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

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

    private void customizeConfigurationFromUrl(JedisClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(getProperties().getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }
}
