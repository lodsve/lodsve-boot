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
package com.lodsve.boot.redis.dynamic;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Map;

/**
 * 多数据源对Lettuce的支持.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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