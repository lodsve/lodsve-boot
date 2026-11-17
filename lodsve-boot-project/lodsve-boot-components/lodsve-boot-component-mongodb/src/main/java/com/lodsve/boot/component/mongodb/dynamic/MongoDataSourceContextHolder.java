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
package com.lodsve.boot.component.mongodb.dynamic;

/**
 * Holds the MongoDB data source selected by the current thread.
 */
public final class MongoDataSourceContextHolder {
    private static final ThreadLocal<String> DATA_SOURCE_NAME = new ThreadLocal<>();

    private MongoDataSourceContextHolder() {
    }

    public static String getCurrentDataSource() {
        return DATA_SOURCE_NAME.get();
    }

    /**
     * Selects a data source for the current scope and restores the previous selection when closed.
     *
     * @param dataSourceName selected data source; a blank value selects the configured default
     * @return scope that restores the previous data source
     */
    public static MongoDataSourceScope open(String dataSourceName) {
        String previousDataSourceName = DATA_SOURCE_NAME.get();
        if (dataSourceName == null || dataSourceName.isBlank()) {
            DATA_SOURCE_NAME.remove();
        } else {
            DATA_SOURCE_NAME.set(dataSourceName);
        }
        return new MongoDataSourceScope(previousDataSourceName);
    }

    static void restore(String dataSourceName) {
        if (dataSourceName == null) {
            DATA_SOURCE_NAME.remove();
        } else {
            DATA_SOURCE_NAME.set(dataSourceName);
        }
    }
}
