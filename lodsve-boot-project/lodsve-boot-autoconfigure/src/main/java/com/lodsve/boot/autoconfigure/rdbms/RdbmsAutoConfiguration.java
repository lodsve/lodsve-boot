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
package com.lodsve.boot.autoconfigure.rdbms;

import com.google.common.collect.Maps;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidFilterConfiguration;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidSpringAopConfiguration;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidStatViewServletConfiguration;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidWebStatFilterConfiguration;
import com.lodsve.boot.component.rdbms.dynamic.DynamicDataSource;
import com.lodsve.boot.component.rdbms.dynamic.DynamicDataSourceAspect;
import com.lodsve.boot.component.rdbms.exception.RdbmsException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * 数据源的配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@EnableConfigurationProperties({RdbmsProperties.class, DruidStatProperties.class})
@ConditionalOnClass(DynamicDataSource.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Slf4j
@Configuration
@Import({
    DruidFilterConfiguration.class,
    DruidStatViewServletConfiguration.class,
    DruidSpringAopConfiguration.class,
    DruidWebStatFilterConfiguration.class
})
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
