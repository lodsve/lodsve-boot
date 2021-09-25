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
package com.lodsve.boot.component.redis.dynamic;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.ReactiveRedisClusterConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Map;

/**
 * 多数据源对Lettuce的支持.
 *
 * @author Hulk Sun
 */
public class DynamicLettuceConnectionFactory extends AbstractDynamicConnectionFactory<LettuceConnectionFactory> implements RedisConnectionFactory, ReactiveRedisConnectionFactory {
    public DynamicLettuceConnectionFactory(String defaultConnectionFactoryName, Map<String, LettuceConnectionFactory> connectionFactories) {
        super(defaultConnectionFactoryName, connectionFactories);
    }

    @Override
    public ReactiveRedisConnection getReactiveConnection() {
        LettuceConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getReactiveConnection();
    }

    @Override
    public ReactiveRedisClusterConnection getReactiveClusterConnection() {
        LettuceConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getReactiveClusterConnection();
    }

    @Override
    public RedisConnection getConnection() {
        LettuceConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        LettuceConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getClusterConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        LettuceConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        LettuceConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getSentinelConnection();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        LettuceConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.translateExceptionIfPossible(e);
    }
}
