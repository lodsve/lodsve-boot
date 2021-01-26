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
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.Map;

/**
 * 多数据源对jedis的支持.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class DynamicJedisConnectionFactory extends AbstractDynamicConnectionFactory<JedisConnectionFactory> implements RedisConnectionFactory {
    public DynamicJedisConnectionFactory(String defaultConnectionFactoryName, Map<String, JedisConnectionFactory> connectionFactories) {
        super(defaultConnectionFactoryName, connectionFactories);
    }

    @Override
    public RedisConnection getConnection() {
        JedisConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        JedisConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getClusterConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        JedisConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getConvertPipelineAndTxResults();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        JedisConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.getSentinelConnection();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        JedisConnectionFactory connectionFactory = getRealConnectionFactory();
        return connectionFactory.translateExceptionIfPossible(ex);
    }
}
