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
import com.mongodb.connection.SslSettings;
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
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.mongodb.autoconfigure.StandardMongoClientSettingsBuilderCustomizer;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
                                         DynamicMongoProperties properties,
                                         MongoProperties mongoProperties,
                                         MongoConnectionDetails defaultConnectionDetails,
                                         ObjectProvider<MongoClientSettings> mongoClientSettings,
                                         ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        String defaultDataSourceName = properties.getDefaultDataSourceName();
        Assert.hasText(defaultDataSourceName, "Default MongoDB data source name must not be blank");

        Map<String, MongoDataSourceProperties> dataSources = properties.getDataSource();
        validateDataSources(dataSources, defaultDataSourceName);

        Map<String, MongoClient> namedClients = new LinkedHashMap<>();
        Map<String, String> databaseNames = new LinkedHashMap<>();

        // 记录默认数据源的数据库名
        String defaultDatabase = getDefaultDatabaseName(mongoProperties, defaultConnectionDetails);
        databaseNames.put(defaultDataSourceName, defaultDatabase);
        MongoClientSettings baseSettings = mongoClientSettings.getIfAvailable(() -> MongoClientSettings.builder().build());
        // Boot 的标准 customizer 会应用默认连接信息，具名数据源不能复用它。
        List<MongoClientSettingsBuilderCustomizer> namedClientCustomizers = builderCustomizers.orderedStream()
            .filter(customizer -> !(customizer instanceof StandardMongoClientSettingsBuilderCustomizer))
            .toList();

        try {
            dataSources.forEach((name, dataSource) -> {
                ConnectionString connectionString = new ConnectionString(dataSource.getUri());
                namedClients.put(name, createMongoClient(connectionString, mongoProperties, baseSettings,
                    namedClientCustomizers));

                String database = connectionString.getDatabase();
                Assert.hasText(database, "MongoDB database name for '" + name + "' must not be blank in URI");
                databaseNames.put(name, database);
            });

            MongoClientRegistry clientRegistry = new MongoClientRegistry(defaultDataSourceName, defaultClient,
                namedClients, databaseNames);
            return DynamicMongoClient.create(clientRegistry);
        } catch (RuntimeException exception) {
            closeNamedClients(namedClients, exception);
            throw exception;
        }
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(MongoDatabaseFactory.class)
    public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier("dynamicMongoClient") MongoClient mongoClient,
                                                    MongoProperties mongoProperties,
                                                    MongoConnectionDetails defaultConnectionDetails) {
        String defaultDatabase = getDefaultDatabaseName(mongoProperties, defaultConnectionDetails);
        // mongoClient 是动态路由客户端，实现了 RegistryHolder 接口
        if (mongoClient instanceof DynamicMongoClient.RegistryHolder) {
            MongoClientRegistry registry = ((DynamicMongoClient.RegistryHolder) mongoClient).getRegistry();
            return new DynamicMongoDatabaseFactory(mongoClient, registry, defaultDatabase);
        }
        return new SimpleMongoClientDatabaseFactory(mongoClient, defaultDatabase);
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

    private String getDefaultDatabaseName(MongoProperties mongoProperties, MongoConnectionDetails connectionDetails) {
        String database = mongoProperties.getDatabase();
        if (database == null) {
            database = connectionDetails.getConnectionString().getDatabase();
        }
        Assert.hasText(database, "Default MongoDB database name must not be blank");
        return database;
    }

    private MongoClient createMongoClient(ConnectionString connectionString, MongoProperties mongoProperties,
                                          MongoClientSettings baseSettings,
                                          List<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        // 为具名客户端创建独立的配置，不继承默认连接的凭据和 TLS 等设置
        MongoClientSettings.Builder settingsBuilder = hasSensitiveConnectionSettings(baseSettings)
            ? MongoClientSettings.builder()
            : MongoClientSettings.builder(baseSettings);
        settingsBuilder.uuidRepresentation(mongoProperties.getRepresentation().getUuid());
        builderCustomizers.forEach(customizer -> customizer.customize(settingsBuilder));
        settingsBuilder.applyConnectionString(connectionString);
        return new MongoClientFactory(List.of()).createMongoClient(settingsBuilder.build());
    }

    private boolean hasSensitiveConnectionSettings(MongoClientSettings settings) {
        SslSettings sslSettings = settings.getSslSettings();
        return settings.getCredential() != null || sslSettings.isEnabled() || sslSettings.isInvalidHostNameAllowed()
            || sslSettings.getContext() != null || settings.getTransportSettings() != null;
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
