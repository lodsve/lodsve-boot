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

import com.lodsve.boot.component.mongodb.dynamic.MongoClientRegistry;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * Routes database access to the current data source's client and database name.
 */
class DynamicMongoDatabaseFactory extends SimpleMongoClientDatabaseFactory {
    private final MongoClientRegistry clientRegistry;

    DynamicMongoDatabaseFactory(MongoClient dynamicClient, MongoClientRegistry clientRegistry, String defaultDatabaseName) {
        super(dynamicClient, defaultDatabaseName);
        this.clientRegistry = clientRegistry;
    }

    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        // 忽略传入的 dbName，使用当前数据源配置的数据库名
        String actualDatabaseName = clientRegistry.getCurrentDatabaseName();
        return clientRegistry.getCurrentClient().getDatabase(actualDatabaseName);
    }
}
