/*
 * Copyright (C) 2019, All rights Reserved, Designed By Technology Middle Platform
 * @Copyright: 2020 www.oppo.com Inc. All rights reserved.
 * 注意：本内容仅限于OPPO公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.lodsve.boot.autoconfigure.redis;

import com.google.common.collect.Maps;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.*;
import com.lodsve.boot.redis.dynamic.DynamicLettuceConnectionFactory;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 多数据源配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisClient.class)
public class DynamicLettuceConnectionConfiguration extends AbstractRedisConnectionConfiguration {
    protected DynamicLettuceConnectionConfiguration(ObjectProvider<RedisProperties> properties,
                                                    ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                                    ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        super(properties, sentinelConfigurationProvider, clusterConfigurationProvider);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    DefaultClientResources lettuceClientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    RedisConnectionFactory redisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        return createLettuceConnectionFactory(builderCustomizers, clientResources);
    }

    private RedisConnectionFactory createLettuceConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        Map<String, LettuceConnectionFactory> factories = Maps.newHashMap();

        // 首先要判断创建多数据源
        if (isDynamicSentinelConnection(getProperties().getSentinels())) {
            // 多数据源 - 哨兵模式
            factories.putAll(createDynamicSentinelConnectionFactory(builderCustomizers, clientResources));
        }

        if (isDynamicClusterConnection(getProperties().getClusters())) {
            // 多数据源 - 集群模式
            factories.putAll(createDynamicClusterConnectionFactory(builderCustomizers, clientResources));
        }

        if (isDynamicSingletonConnection(getProperties().getSingletons())) {
            // 多数据源 - 单实例模式
            factories.putAll(createDynamicSingletonConnectionFactory(builderCustomizers, clientResources));
        }

        if (getSentinelConfig(getProperties().getSentinel()) != null) {
            // 单数据源 - 哨兵模式
            LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
            factories.put("LettuceConnectionFactory-Sentinel", new LettuceConnectionFactory(Objects.requireNonNull(getSentinelConfig(getProperties().getSentinel())), clientConfig));
        }
        if (getClusterConfiguration(getProperties().getCluster()) != null) {
            // 单数据源 - 集群模式
            LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
            factories.put("LettuceConnectionFactory-Cluster", new LettuceConnectionFactory(Objects.requireNonNull(getClusterConfiguration(getProperties().getCluster())), clientConfig));
        }

        // 单数据源 - 单实例模式
        RedisStandaloneConfiguration configuration = getStandaloneConfig(getProperties().getUrl(), getProperties().getHost(), getProperties().getPort(), getProperties().getPassword(), getProperties().getDatabase());
        if (null != configuration) {
            LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
            factories.put("LettuceConnectionFactory-Standalone", new LettuceConnectionFactory(configuration, clientConfig));
        }

        AtomicReference<String> defaultName = new AtomicReference<>();
        AtomicInteger index = new AtomicInteger(0);
        factories.forEach((key, value) -> {
            if (0 == index.getAndIncrement()) {
                defaultName.set(key);
            }
            value.setShareNativeConnection(getProperties().isShareNativeConnection());
            value.afterPropertiesSet();
        });

        return new DynamicLettuceConnectionFactory(StringUtils.isEmpty(getProperties().getDefaultName()) ? defaultName.get() : getProperties().getDefaultName(), factories);
    }

    private Map<String, LettuceConnectionFactory> createDynamicSingletonConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        Map<String, Singleton> singletons = getProperties().getSingletons();
        Map<String, LettuceConnectionFactory> factories = Maps.newHashMap();
        for (String name : singletons.keySet()) {
            Singleton singleton = singletons.get(name);
            RedisStandaloneConfiguration configuration = getStandaloneConfig("", singleton.getHost(), singleton.getPort(), singleton.getPassword(), singleton.getDatabase());
            if (null == configuration) {
                continue;
            }
            LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
            LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfig);
            factories.put(name, factory);
        }

        return factories;
    }

    private Map<String, LettuceConnectionFactory> createDynamicClusterConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        Map<String, Cluster> clusters = getProperties().getClusters();
        Map<String, LettuceConnectionFactory> factories = Maps.newHashMap();
        for (String name : clusters.keySet()) {
            RedisClusterConfiguration configuration = getClusterConfiguration(clusters.get(name));
            if (null == configuration) {
                continue;
            }
            LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
            LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfig);
            factories.put(name, factory);
        }

        return factories;
    }

    private Map<String, LettuceConnectionFactory> createDynamicSentinelConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        Map<String, Sentinel> sentinels = getProperties().getSentinels();
        Map<String, LettuceConnectionFactory> factories = Maps.newHashMap();
        for (String name : sentinels.keySet()) {
            RedisSentinelConfiguration configuration = getSentinelConfig(sentinels.get(name));
            if (null == configuration) {
                continue;
            }
            LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
            LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfig);
            factories.put(name, factory);
        }

        return factories;
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, Pool pool) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientOptions(initializeClientOptionsBuilder().timeoutOptions(TimeoutOptions.enabled()).build());
        builder.clientResources(clientResources);
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new PoolBuilderFactory().createBuilder(pool);
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder applyProperties(
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (getProperties().isSsl()) {
            builder.useSsl();
        }
        if (getProperties().getTimeout() != null) {
            builder.commandTimeout(getProperties().getTimeout());
        }
        if (getProperties().getLettuce() != null) {
            Lettuce lettuce = getProperties().getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(getProperties().getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(getProperties().getClientName())) {
            builder.clientName(getProperties().getClientName());
        }
        return builder;
    }

    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (getProperties().getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = getProperties().getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder();
            if (refreshProperties.getPeriod() != null) {
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }
            if (refreshProperties.isAdaptive()) {
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }
            return builder.topologyRefreshOptions(refreshBuilder.build());
        }
        return ClientOptions.builder();
    }

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = parseUrl(getProperties().getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    /**
     * Inner class to allow optional commons-pool2 dependency.
     */
    private static class PoolBuilderFactory {

        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
            }
            if (properties.getMaxWait() != null) {
                config.setMaxWaitMillis(properties.getMaxWait().toMillis());
            }
            return config;
        }

    }
}
