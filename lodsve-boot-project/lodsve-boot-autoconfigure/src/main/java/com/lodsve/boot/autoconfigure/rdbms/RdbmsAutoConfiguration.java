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
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 数据源的配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@EnableConfigurationProperties(RdbmsProperties.class)
@ConditionalOnClass(DynamicDataSource.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Configuration
public class RdbmsAutoConfiguration {
    private static final String DATA_SOURCE_TYPE_NAME_HIKARI = "com.zaxxer.hikari.HikariDataSource";

    private final RdbmsProperties rdbmsProperties;

    public RdbmsAutoConfiguration(RdbmsProperties rdbmsProperties) {
        this.rdbmsProperties = rdbmsProperties;
    }

    @Bean
    public DataSource dataSource() {
        Map<String, DataSourceProperty> dataSourceProperties = rdbmsProperties.getDataSourceProperties();
        String defaultDataSourceName = rdbmsProperties.getDefaultDataSourceName();
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();

        dataSourceProperties.forEach((name, properties) -> {
            DataSource dataSource = buildDataSourcePool(properties);
            dataSourceMap.put(name, dataSource);
        });

        return new DynamicDataSource(dataSourceMap, defaultDataSourceName);
    }

    private DataSource buildDataSourcePool(DataSourceProperty properties) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (ClassUtils.isPresent(DATA_SOURCE_TYPE_NAME_HIKARI, classLoader)) {
            // hikari pool
            return createHikariDataSource(properties);
        } else {
            // no datasource pool type
            throw new IllegalStateException("No supported DataSource pool type found");
        }
    }

    private DataSource createHikariDataSource(DataSourceProperty properties) {
        HikariCpConfig globalConfig = rdbmsProperties.getHikari();
        HikariCpConfig config = properties.getHikari();
        HikariConfig hikariConfig = config.toHikariConfig(globalConfig);

        hikariConfig.setUsername(properties.getUsername());
        hikariConfig.setPassword(properties.getPassword());
        hikariConfig.setJdbcUrl(properties.getUrl());
        hikariConfig.setPoolName(properties.getPoolName());
        String driverClassName = properties.getDriverClassName();
        if (!StringUtils.isEmpty(driverClassName)) {
            config.setDriverClassName(driverClassName);
        }
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
