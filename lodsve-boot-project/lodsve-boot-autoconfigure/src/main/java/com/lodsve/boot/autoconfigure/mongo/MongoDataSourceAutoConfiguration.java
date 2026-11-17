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
package com.lodsve.boot.autoconfigure.mongo;

import com.lodsve.boot.component.mongodb.dynamic.DynamicMongoClient;
import com.lodsve.boot.component.mongodb.dynamic.MongoClientRegistry;
import com.lodsve.boot.component.mongodb.dynamic.MongoDataSourceAspect;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoClientFactory;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 为 Spring Data MongoDB 提供多数据源路由客户端。
 */
@AutoConfiguration(after = MongoAutoConfiguration.class, before = DataMongoAutoConfiguration.class)
@ConditionalOnClass({MongoClient.class, MongoClientSettings.class, MongoDatabaseFactory.class})
@ConditionalOnBean(name = "mongo")
@Conditional(MongoDataSourceConfiguredCondition.class)
@EnableConfigurationProperties(DynamicMongoProperties.class)
public class MongoDataSourceAutoConfiguration {

    /**
     * 创建路由客户端。ClientSession 绑定到具体 MongoClient，不能在切换数据源后复用。
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "dynamicMongoClient")
    public MongoClient dynamicMongoClient(@Qualifier("mongo") MongoClient defaultClient,
                                         MongoClientSettings baseSettings,
                                         ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                                         DynamicMongoProperties properties) {
        String defaultDataSourceName = properties.getDefaultDataSourceName();
        Assert.hasText(defaultDataSourceName, "Default MongoDB data source name must not be blank");

        Map<String, MongoDataSourceProperties> dataSources = properties.getDataSource();
        Assert.notEmpty(dataSources, "At least one named MongoDB data source must be configured");
        validateDataSources(dataSources, defaultDataSourceName);

        List<MongoClientSettingsBuilderCustomizer> orderedCustomizers = builderCustomizers.orderedStream().toList();
        Map<String, MongoClient> namedClients = new LinkedHashMap<>();
        try {
            dataSources.forEach((name, dataSource) -> namedClients.put(name,
                createMongoClient(dataSource.getUri(), baseSettings, orderedCustomizers)));
            MongoClientRegistry clientRegistry = new MongoClientRegistry(defaultDataSourceName, defaultClient,
                namedClients);
            return DynamicMongoClient.create(clientRegistry);
        } catch (RuntimeException exception) {
            closeNamedClients(namedClients, exception);
            throw exception;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoDataSourceAspect mongoDataSourceAspect() {
        return new MongoDataSourceAspect();
    }

    private void validateDataSources(Map<String, MongoDataSourceProperties> dataSources, String defaultDataSourceName) {
        dataSources.forEach((name, properties) -> {
            Assert.hasText(name, "MongoDB data source name must not be blank");
            Assert.isTrue(!defaultDataSourceName.equals(name), "MongoDB data source name '" + name
                + "' is reserved for the default client");
            Assert.notNull(properties, "MongoDB data source properties for '" + name + "' must not be null");
            Assert.isTrue(StringUtils.hasText(properties.getUri()),
                "MongoDB data source URI for '" + name + "' must not be blank");
        });
    }

    private MongoClient createMongoClient(String uri, MongoClientSettings baseSettings,
                                         List<MongoClientSettingsBuilderCustomizer> orderedCustomizers) {
        ConnectionString connectionString = new ConnectionString(uri);
        List<MongoClientSettingsBuilderCustomizer> customizers = new ArrayList<>(orderedCustomizers);
        customizers.add(settings -> settings.applyConnectionString(connectionString));
        return new MongoClientFactory(customizers).createMongoClient(baseSettings);
    }

    private void closeNamedClients(Map<String, MongoClient> namedClients, RuntimeException originalFailure) {
        namedClients.values().forEach(client -> {
            try {
                client.close();
            } catch (RuntimeException closeFailure) {
                originalFailure.addSuppressed(closeFailure);
            }
        });
    }
}
