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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * redis配置.
 *
 * @author Hulk Sun
 */
@Data
@ConfigurationProperties("lodsve.redis")
public class RedisProperties {
    /**
     * 默认数据源名称，如果不填，则默认取第一个，可能会带来不稳定
     */
    private String defaultName;

    /**
     * Whether to enable SSL support.
     */
    private boolean ssl;

    /**
     * Connection timeout.
     */
    private Duration timeout;

    /**
     * Client name to be set on connections with CLIENT SETNAME.
     */
    private String clientName;
    /**
     * 单数据源-单redis
     */
    @NestedConfigurationProperty
    private Singleton singleton;
    /**
     * 单数据源-Sentinel
     */
    @NestedConfigurationProperty
    private Sentinel sentinel;
    /**
     * 单数据源-Cluster
     */
    @NestedConfigurationProperty
    private Cluster cluster;
    /**
     * 多数据源 - 单节点redis
     */
    @NestedConfigurationProperty
    private Map<String, Singleton> singletons;
    /**
     * 多数据源 - 哨兵模式
     */
    @NestedConfigurationProperty
    private Map<String, Sentinel> sentinels;
    /**
     * 多数据源 - 集群模式
     */
    @NestedConfigurationProperty
    private Map<String, Cluster> clusters;

    private final Jedis jedis = new Jedis();

    private final Lettuce lettuce = new Lettuce();
    /**
     * redis每个key的实效时间
     */
    private Map<String, Long> keyTtl;
    /**
     * redis缓存的有效时间单位是秒 0代表永久有效
     */
    private long defaultExpiration = 0;

    /**
     * Pool properties.
     */
    @Data
    public static class Pool {
        /**
         * Maximum number of "idle" connections in the pool. Use a negative value to
         * indicate an unlimited number of idle connections.
         */
        private int maxIdle = 8;
        /**
         * Target for the minimum number of idle connections to maintain in the pool. This
         * setting only has an effect if both it and time between eviction runs are
         * positive.
         */
        private int minIdle = 0;
        /**
         * Maximum number of connections that can be allocated by the pool at a given
         * time. Use a negative value for no limit.
         */
        private int maxActive = 8;
        /**
         * Maximum amount of time a connection allocation should block before throwing an
         * exception when the pool is exhausted. Use a negative value to block
         * indefinitely.
         */
        private Duration maxWait = Duration.ofMillis(-1);
        /**
         * Time between runs of the idle object evictor thread. When positive, the idle
         * object evictor thread starts, otherwise no idle object eviction is performed.
         */
        private Duration timeBetweenEvictionRuns;
    }

    /**
     * singleton redis
     */
    @Data
    public static class Singleton {
        /**
         * Redis server host.
         */
        private String host;

        /**
         * Login password of the redis server.
         */
        private String password;

        /**
         * Redis server port.
         */
        private int port = 6379;
        /**
         * 数据库索引
         */
        private int database = 0;
    }

    /**
     * Cluster properties.
     */
    @Data
    public static class Cluster {
        /**
         * Comma-separated list of "host:port" pairs to bootstrap from. This represents an
         * "initial" list of cluster nodes and is required to have at least one entry.
         */
        private List<String> nodes;

        /**
         * Password for authenticating with sentinel(s).
         */
        private String password;

        /**
         * Maximum number of redirects to follow when executing commands across the
         * cluster.
         */
        private Integer maxRedirects;
    }

    /**
     * Redis sentinel properties.
     */
    @Data
    public static class Sentinel {
        /**
         * Name of the Redis server.
         */
        private String master;

        /**
         * Comma-separated list of "host:port" pairs.
         */
        private List<String> nodes;

        /**
         * Password for authenticating with data none(s).
         */
        private String sentinelPassword;

        /**
         * Password for authenticating with sentinel(s).
         */
        private String password;
        /**
         * 数据库索引
         */
        private int database = 0;
    }

    /**
     * Jedis client properties.
     */
    @Data
    public static class Jedis {
        /**
         * Jedis pool configuration.
         */
        private Pool pool;
    }

    /**
     * Lettuce client properties.
     */
    @Data
    public static class Lettuce {
        /**
         * 表示多个LettuceConnection将共享一个native connection
         */
        private boolean shareNativeConnection = true;
        /**
         * Shutdown timeout.
         */
        private Duration shutdownTimeout = Duration.ofMillis(100);
        /**
         * Lettuce pool configuration.
         */
        private Pool pool;
        private final Cluster cluster = new Cluster();

        @Data
        public static class Cluster {
            private final Refresh refresh = new Refresh();

            @Data
            public static class Refresh {

                /**
                 * Cluster topology refresh period.
                 */
                private Duration period;

                /**
                 * Whether adaptive topology refreshing using all available refresh
                 * triggers should be used.
                 */
                private boolean adaptive;
            }
        }
    }
}

