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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 数据源的配置.
 *
 * @author Hulk Sun
 */
@ConfigurationProperties(prefix = "lodsve.rdbms")
public class RdbmsProperties {
    /**
     * 默认数据源名称，不填为{@link #dataSource}的第一个值
     */
    private String defaultDataSourceName;
    /**
     * 多数据源配置
     * 数据源名称 -> 数据源配置
     */
    private Map<String, DataSourceProperty> dataSource;

    public String getDefaultDataSourceName() {
        return defaultDataSourceName;
    }

    public void setDefaultDataSourceName(String defaultDataSourceName) {
        this.defaultDataSourceName = defaultDataSourceName;
    }

    public Map<String, DataSourceProperty> getDataSource() {
        return dataSource;
    }

    public void setDataSource(Map<String, DataSourceProperty> dataSource) {
        this.dataSource = dataSource;
    }
}
