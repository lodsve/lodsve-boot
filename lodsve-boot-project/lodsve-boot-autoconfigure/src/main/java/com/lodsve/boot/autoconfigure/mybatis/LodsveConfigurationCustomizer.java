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
package com.lodsve.boot.autoconfigure.mybatis;

import com.lodsve.boot.component.mybatis.exception.MyBatisException;
import com.lodsve.boot.component.mybatis.plugins.pagination.PaginationInterceptor;
import com.lodsve.boot.component.mybatis.plugins.repository.BaseRepositoryInterceptor;
import com.lodsve.boot.component.mybatis.type.TypeHandlerScanner;
import com.lodsve.boot.component.mybatis.utils.MyBatisUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;

/**
 * 扩展mybatis的configuration，加入以下内容：.
 * <ol>
 * <li>字段名和数据库column名符合驼峰命名规范</li>
 * <li>加入两个个插件：分页、通用DAO</li>
 * </ol>
 * 这样就可以省略mybatis的所有基本配置
 *
 * @author Hulk Sun
 */
public class LodsveConfigurationCustomizer implements ConfigurationCustomizer {
    private final MybatisProperties properties;
    private final DataSource dataSource;

    public LodsveConfigurationCustomizer(MybatisProperties properties, DataSource dataSource) {
        this.properties = properties;
        this.dataSource = dataSource;
    }

    @Override
    public void customize(Configuration configuration) {
        configuration.setMapUnderscoreToCamelCase(properties.isMapUnderscoreToCamelCase());

        configuration.addInterceptor(new PaginationInterceptor());
        configuration.addInterceptor(new BaseRepositoryInterceptor());

        if (ArrayUtils.isNotEmpty(properties.getEnumsLocations())) {
            TypeHandler<?>[] handlers = new TypeHandlerScanner().find(properties.getEnumsLocations());
            Arrays.stream(handlers).forEach(configuration.getTypeHandlerRegistry()::register);
        }

        // 目前仅支持默认数据库，后续再优化
        try (Connection connection = dataSource.getConnection()) {
            String dbName = connection.getMetaData().getDatabaseProductName().toLowerCase();
            MyBatisUtils.setDbType(dbName);
        } catch (Exception e) {
            throw new MyBatisException("When setting the database type, the database product name is empty.");
        }
    }
}
