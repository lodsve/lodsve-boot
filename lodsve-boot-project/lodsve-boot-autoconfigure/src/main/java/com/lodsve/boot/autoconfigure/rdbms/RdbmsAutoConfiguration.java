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

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidFilterConfiguration;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidSpringAopConfiguration;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidStatViewServletConfiguration;
import com.lodsve.boot.autoconfigure.rdbms.druid.DruidWebStatFilterConfiguration;
import com.lodsve.boot.component.rdbms.dynamic.DynamicDataSource;
import com.lodsve.boot.component.rdbms.dynamic.DynamicDataSourceAspect;
import com.lodsve.boot.component.rdbms.exception.RdbmsException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * 数据源的配置.
 *
 * @author Hulk Sun
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
    private static final Logger logger = LoggerFactory.getLogger(RdbmsAutoConfiguration.class);
    private final RdbmsProperties rdbmsProperties;

    public RdbmsAutoConfiguration(RdbmsProperties rdbmsProperties) {
        this.rdbmsProperties = rdbmsProperties;
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
        PoolSetting.PoolType type = properties.getPoolSetting().getType();
        switch (type) {
            case Druid:
                return createDruidDataSource(dataSourceName, properties);
            case Hikari:
            default:
                return createHikariDataSource(dataSourceName, properties);
        }
    }

    private DataSource createHikariDataSource(String dataSourceName, DataSourceProperty properties) {
        // 数据源连接池配置
        HikariConfig config = new HikariConfig();
        PoolSetting setting = properties.getPoolSetting();

        config.setPoolName(mergeProperty(properties.getPoolName(), config.getPoolName(), dataSourceName + "-pool"));
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setJdbcUrl(properties.getUrl());
        config.setMaximumPoolSize(setting.getMaxActive());
        config.setMinimumIdle(setting.getMinIdle());
        config.setConnectionTimeout(setting.getMaxWait());
        config.setConnectionTestQuery(setting.getValidationQuery());
        String driverClassName = properties.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }

        // 有额外配置
        handleExtProperties(config, setting.getExtProperties());

        return new HikariDataSource(config);
    }

    private void handleExtProperties(Object obj, Properties extProperties) {
        if (null == extProperties || extProperties.isEmpty()) {
            return;
        }
        BeanWrapper wrapper = new BeanWrapperImpl(obj);

        // 1. 遍历extProperties
        extProperties.forEach((k, v) -> {
            try {
                wrapper.setPropertyValue((String) k, v);
            } catch (BeansException e) {
                // 异常：字段不存在，则忽略
                logger.error("Can't find field '{}' in class '{}'", k, obj.getClass().getName());
            }
        });
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
        DruidDataSource config = new DruidDataSource();
        PoolSetting setting = properties.getPoolSetting();

        config.setName(mergeProperty(properties.getPoolName(), config.getName(), dataSourceName + "-pool"));
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setUrl(properties.getUrl());
        config.setInitialSize(setting.getInitSize());
        config.setMaxActive(setting.getMaxActive());
        config.setMinIdle(setting.getMinIdle());
        config.setMaxWait(setting.getMaxWait());
        config.setValidationQuery(setting.getValidationQuery());
        config.setTestOnBorrow(setting.isTestOnBorrow());
        config.setTestOnReturn(setting.isTestOnReturn());
        config.setTestWhileIdle(setting.isTestWhileIdle());
        config.setTimeBetweenEvictionRunsMillis(setting.getTimeBetweenEvictionRunsMillis());
        config.setMinEvictableIdleTimeMillis(setting.getMinEvictableIdleTimeMillis());
        config.setRemoveAbandoned(setting.isRemoveAbandoned());
        config.setRemoveAbandonedTimeout(setting.getRemoveAbandonedTimeout());
        config.setLogAbandoned(setting.isLogAbandoned());

        String driverClassName = properties.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }

        // 有额外配置
        handleExtProperties(config, setting.getExtProperties());

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
