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
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    private final RdbmsProperties rdbmsProperties;

    public RdbmsAutoConfiguration(RdbmsProperties rdbmsProperties) {
        this.rdbmsProperties = rdbmsProperties;
    }

    @Bean
    public DataSource dataSource() {
        Map<String, DataSourceProperties> dataSourceProperties = rdbmsProperties.getDataSourceProperties();
        String defaultDataSourceName = rdbmsProperties.getDefaultDataSourceName();
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();

        dataSourceProperties.forEach((k, v) -> {
            DataSource dataSource = v.initializeDataSourceBuilder().build();
            dataSourceMap.put(k, dataSource);
        });

        return new DynamicDataSource(dataSourceMap, defaultDataSourceName);
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }
}
