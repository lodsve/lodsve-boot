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
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
