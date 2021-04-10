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
