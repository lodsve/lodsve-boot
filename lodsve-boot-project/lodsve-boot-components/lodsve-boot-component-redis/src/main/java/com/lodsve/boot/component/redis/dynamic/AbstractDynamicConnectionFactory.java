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

import io.lettuce.core.RedisConnectionException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Map;

/**
 * 多数据源.
 *
 * @author Hulk Sun
 */
public abstract class AbstractDynamicConnectionFactory<T extends RedisConnectionFactory & DisposableBean> implements DisposableBean, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDynamicConnectionFactory.class);
    private final String defaultConnectionFactoryName;
    private final Map<String, T> connectionFactories;

    public AbstractDynamicConnectionFactory(String defaultConnectionFactoryName, Map<String, T> connectionFactories) {
        this.defaultConnectionFactoryName = defaultConnectionFactoryName;
        this.connectionFactories = connectionFactories;
    }

    protected T getRealConnectionFactory() {
        String connectionFactoryName = RedisConnectionHolder.getInstance().get();
        connectionFactoryName = (StringUtils.isBlank(connectionFactoryName) ? defaultConnectionFactoryName : connectionFactoryName);
        T connectionFactory = connectionFactories.get(connectionFactoryName);
        if (null == connectionFactory) {
            throw new RedisConnectionException(String.format("Connection factory(%s) not found!", connectionFactoryName));
        }

        return connectionFactory;
    }

    @Override
    public void destroy() throws Exception {
        if (MapUtils.isEmpty(connectionFactories)) {
            return;
        }

        for (T connection : connectionFactories.values()) {
            try {
                connection.destroy();
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("destroy connection factory error!");
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("init dynamic redis connection factory!");
        }
    }
}
