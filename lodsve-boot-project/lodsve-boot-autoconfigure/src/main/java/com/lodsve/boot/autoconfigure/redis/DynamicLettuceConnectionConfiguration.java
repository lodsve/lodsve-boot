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
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Lettuce;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Pool;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Sentinel;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Singleton;
import com.lodsve.boot.component.redis.dynamic.DynamicLettuceConnectionFactory;
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
        Singleton singleton = getProperties().getSingleton();
        if (singleton != null) {
            RedisStandaloneConfiguration configuration = getStandaloneConfig(singleton.getHost(), singleton.getPort(), singleton.getPassword(), singleton.getDatabase());
            if (null != configuration) {
                LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(builderCustomizers, clientResources, getProperties().getLettuce().getPool());
                factories.put("LettuceConnectionFactory-Standalone", new LettuceConnectionFactory(configuration, clientConfig));
            }
        }

        AtomicReference<String> defaultName = new AtomicReference<>();
        AtomicInteger index = new AtomicInteger(0);
        factories.forEach((key, value) -> {
            if (0 == index.getAndIncrement()) {
                defaultName.set(key);
            }
            value.setShareNativeConnection(getProperties().getLettuce().isShareNativeConnection());
            value.afterPropertiesSet();
        });

        return new DynamicLettuceConnectionFactory(StringUtils.isEmpty(getProperties().getDefaultName()) ? defaultName.get() : getProperties().getDefaultName(), factories);
    }

    private Map<String, LettuceConnectionFactory> createDynamicSingletonConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources) {
        Map<String, Singleton> singletons = getProperties().getSingletons();
        Map<String, LettuceConnectionFactory> factories = Maps.newHashMap();
        for (String name : singletons.keySet()) {
            Singleton singleton = singletons.get(name);
            RedisStandaloneConfiguration configuration = getStandaloneConfig(singleton.getHost(), singleton.getPort(), singleton.getPassword(), singleton.getDatabase());
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

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
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
