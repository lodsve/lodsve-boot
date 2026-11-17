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

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Collections;
import java.util.Map;

/**
 * 仅在配置了至少一个具名 MongoDB 数据源时启用路由。
 */
public class MongoDataSourceConfiguredCondition implements Condition {
    private static final String DATA_SOURCE_PROPERTY = "lodsve.mongo.data-source";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, MongoDataSourceProperties> dataSources = Binder.get(context.getEnvironment())
            .bind(DATA_SOURCE_PROPERTY, Bindable.mapOf(String.class, MongoDataSourceProperties.class))
            .orElse(Collections.emptyMap());
        return !dataSources.isEmpty();
    }
}
