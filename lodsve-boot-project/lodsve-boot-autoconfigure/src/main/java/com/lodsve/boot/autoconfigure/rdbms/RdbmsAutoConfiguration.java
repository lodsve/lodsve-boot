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
package com.lodsve.boot.autoconfigure.rdbms;

import com.google.common.collect.Maps;
import com.lodsve.boot.rdbms.dynamic.DynamicDataSource;
import com.lodsve.boot.rdbms.dynamic.DynamicDataSourceAspect;
import com.lodsve.boot.rdbms.exception.RdbmsException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 数据源的配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@EnableConfigurationProperties(RdbmsProperties.class)
@ConditionalOnClass(DynamicDataSource.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Slf4j
@Configuration
public class RdbmsAutoConfiguration {
    private static final String DATA_SOURCE_TYPE_NAME_HIKARI = "com.zaxxer.hikari.HikariDataSource";
    private static final String DATA_SOURCE_TYPE_NAME_DRUID = "com.alibaba.druid.pool.DruidDataSource";
    private final boolean druidEnabled;
    private final boolean hikariEnabled;

    private final RdbmsProperties rdbmsProperties;

    public RdbmsAutoConfiguration(RdbmsProperties rdbmsProperties) {
        this.rdbmsProperties = rdbmsProperties;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        druidEnabled = ClassUtils.isPresent(DATA_SOURCE_TYPE_NAME_DRUID, classLoader);
        hikariEnabled = ClassUtils.isPresent(DATA_SOURCE_TYPE_NAME_HIKARI, classLoader);
    }

    @Bean
    public DataSource dataSource() {
        Map<String, DataSourceProperty> dataSourceProperties = rdbmsProperties.getDataSource();
        String defaultDataSourceName = rdbmsProperties.getDefaultDataSourceName();
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();

        dataSourceProperties.forEach((name, properties) -> {
            DataSource dataSource = buildDataSourcePool(name, properties);
            dataSourceMap.put(name, dataSource);
        });

        return new DynamicDataSource(dataSourceMap, defaultDataSourceName);
    }

    private DataSource buildDataSourcePool(String dataSourceName, DataSourceProperty properties) {
        Class<? extends DataSource> type = properties.getType();
        if (type == null) {
            if (druidEnabled) {
                // druid pool
                return createDruidDataSource(dataSourceName, properties);
            } else if (hikariEnabled) {
                // hikari pool
                return createHikariDataSource(dataSourceName, properties);
            } else {
                // no datasource pool type
                throw new IllegalStateException("No supported DataSource pool type found");
            }
        } else if (DATA_SOURCE_TYPE_NAME_DRUID.equals(type.getName())) {
            // druid pool
            return createDruidDataSource(dataSourceName, properties);
        } else if (DATA_SOURCE_TYPE_NAME_HIKARI.equals(type.getName())) {
            // hikari pool
            return createHikariDataSource(dataSourceName, properties);
        } else {
            // no datasource pool type
            throw new IllegalStateException("No supported DataSource pool type found");
        }
    }

    private DataSource createHikariDataSource(String dataSourceName, DataSourceProperty properties) {
        // 数据源连接池配置
        HikariCpConfig config = properties.getHikari();

        config.setPoolName(mergeProperty(properties.getPoolName(), config.getPoolName(), dataSourceName + "-pool"));
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setJdbcUrl(properties.getUrl());
        String driverClassName = properties.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }

        return new HikariDataSource(config);
    }

    private String mergeProperty(String global, String dataSourceProp, String defaultValue) {
        if (StringUtils.isNoneBlank(dataSourceProp)) {
            return dataSourceProp;
        } else if (StringUtils.isNoneBlank(global)) {
            return defaultValue;
        } else {
            return defaultValue;
        }
    }

    private DataSource createDruidDataSource(String dataSourceName, DataSourceProperty properties) {
        // 数据源内部配置
        DruidCpConfig config = properties.getDruid();

        config.setName(mergeProperty(properties.getPoolName(), config.getName(), dataSourceName + "-pool"));
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setUrl(properties.getUrl());
        String driverClassName = properties.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }

        try {
            config.init();
        } catch (SQLException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            throw new RdbmsException("build druid datasource error!");
        }
        return config;
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
