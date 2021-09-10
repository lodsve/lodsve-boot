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

import com.lodsve.boot.autoconfigure.redis.RedisProperties.Cluster;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Sentinel;
import com.lodsve.boot.autoconfigure.redis.RedisProperties.Singleton;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base Redis connection configuration.
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 * @author Alen Turkovic
 * @author Scott Frederick
 */
public abstract class AbstractRedisConnectionConfiguration {

    private final RedisProperties properties;

    private final RedisSentinelConfiguration sentinelConfiguration;

    private final RedisClusterConfiguration clusterConfiguration;

    protected AbstractRedisConnectionConfiguration(ObjectProvider<RedisProperties> properties,
                                                   ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                                   ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        this.properties = properties.getIfAvailable();
        this.sentinelConfiguration = sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = clusterConfigurationProvider.getIfAvailable();
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig(String host, int port, String password, int database) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(host)) {
            config.setHostName(host);
            config.setPort(port);
            config.setPassword(RedisPassword.of(password));
        } else {
            return null;
        }
        config.setDatabase(database);
        return config;
    }

    protected final RedisSentinelConfiguration getSentinelConfig(Sentinel sentinelProperties) {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        }
        if (sentinelProperties == null) {
            return null;
        }

        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.master(sentinelProperties.getMaster());
        config.setSentinels(createSentinels(sentinelProperties));

        if (sentinelProperties.getPassword() != null) {
            config.setPassword(RedisPassword.of(sentinelProperties.getPassword()));
        }
        if (sentinelProperties.getSentinelPassword() != null) {
            config.setSentinelPassword(RedisPassword.of(sentinelProperties.getSentinelPassword()));
        }
        config.setDatabase(sentinelProperties.getDatabase());
        return config;
    }

    /**
     * Create a {@link RedisClusterConfiguration} if necessary.
     *
     * @return {@literal null} if no cluster settings are set.
     */
    protected final RedisClusterConfiguration getClusterConfiguration(Cluster clusterProperties) {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        }
        if (clusterProperties == null) {
            return null;
        }

        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        String password = clusterProperties.getPassword();
        if (password != null) {
            config.setPassword(RedisPassword.of(password));
        }
        return config;
    }

    protected final RedisProperties getProperties() {
        return this.properties;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    protected boolean isDynamicSentinelConnection(Map<String, Sentinel> sentinels) {
        return MapUtils.isNotEmpty(sentinels);
    }

    protected boolean isDynamicClusterConnection(Map<String, Cluster> clusters) {
        return MapUtils.isNotEmpty(clusters);
    }

    protected boolean isDynamicSingletonConnection(Map<String, Singleton> singletons) {
        return MapUtils.isNotEmpty(singletons);
    }
}
