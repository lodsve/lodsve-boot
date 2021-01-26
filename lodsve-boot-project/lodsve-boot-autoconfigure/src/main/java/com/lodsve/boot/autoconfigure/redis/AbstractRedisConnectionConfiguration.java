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

import java.net.URI;
import java.net.URISyntaxException;
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

    protected final RedisStandaloneConfiguration getStandaloneConfig(String url, String host, int port, String password, int database) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(url)) {
            ConnectionInfo connectionInfo = parseUrl(url);
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else if (StringUtils.hasText(host)) {
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
        if (this.properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(this.properties.getPassword()));
        }
        if (sentinelProperties.getPassword() != null) {
            config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
        }
        config.setDatabase(this.properties.getDatabase());
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
        String password = null;
        if (clusterProperties.getPassword() != null) {
            password = clusterProperties.getPassword();
        } else if (properties.getPassword() != null) {
            password = properties.getPassword();
        }
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

    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            }
            boolean useSsl = ("rediss".equals(scheme));
            String password = null;
            if (uri.getUserInfo() != null) {
                password = uri.getUserInfo();
                int index = password.indexOf(':');
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
            }
            return new ConnectionInfo(uri, useSsl, password);
        } catch (URISyntaxException ex) {
            throw new RedisUrlSyntaxException(url, ex);
        }
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

    static class ConnectionInfo {

        private final URI uri;

        private final boolean useSsl;

        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.password = password;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        String getHostName() {
            return this.uri.getHost();
        }

        int getPort() {
            return this.uri.getPort();
        }

        String getPassword() {
            return this.password;
        }

    }


    private static class RedisUrlSyntaxException extends RuntimeException {

        private final String url;

        RedisUrlSyntaxException(String url, Exception cause) {
            super(buildMessage(url), cause);
            this.url = url;
        }

        RedisUrlSyntaxException(String url) {
            super(buildMessage(url));
            this.url = url;
        }

        String getUrl() {
            return this.url;
        }

        private static String buildMessage(String url) {
            return "Invalid Redis URL '" + url + "'";
        }

    }
}
