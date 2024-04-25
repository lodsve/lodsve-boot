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
package com.lodsve.boot.component.rdbms.dynamic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

/**
 * 动态数据源.
 *
 * @author Hulk Sun
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private final Map<String, DataSource> dataSourceMap;
    private DataSource defaultDataSource;

    public DynamicDataSource(Map<String, DataSource> dataSourceMap, String defaultDataSourceName) {
        this.dataSourceMap = dataSourceMap;

        Collection<DataSource> values = dataSourceMap.values();
        defaultDataSource = Lists.newArrayList(values).get(0);
        if (StringUtils.isBlank(defaultDataSourceName)) {
            defaultDataSource = Lists.newArrayList(values).get(0);
        } else {
            defaultDataSource = dataSourceMap.get(defaultDataSourceName);
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getInstance().get();
    }

    @Override
    public void afterPropertiesSet() {
        if (MapUtils.isEmpty(dataSourceMap)) {
            throw new IllegalArgumentException("Property 'dataSourceMap' is required");
        }

        Map<Object, Object> targetDataSources = Maps.newHashMap();
        targetDataSources.putAll(dataSourceMap);

        super.setTargetDataSources(targetDataSources);

        super.setTargetDataSources(targetDataSources);
        super.setDefaultTargetDataSource(defaultDataSource);

        super.afterPropertiesSet();
    }
}
