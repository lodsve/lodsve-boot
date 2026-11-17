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

import com.mongodb.client.MongoClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Resolves the current MongoDB client and manages clients created for named sources.
 */
public final class MongoClientRegistry {
    private final String defaultDataSourceName;
    private final MongoClient defaultClient;
    private final Map<String, MongoClient> clients;
    private final Map<String, String> databaseNames;
    private final List<MongoClient> managedClients;
    private final AtomicBoolean managedClientsClosed = new AtomicBoolean();

    public MongoClientRegistry(String defaultDataSourceName, MongoClient defaultClient,
                               Map<String, MongoClient> namedClients, Map<String, String> databaseNames) {
        if (defaultDataSourceName == null || defaultDataSourceName.isBlank()) {
            throw new IllegalArgumentException("Default MongoDB data source name must not be blank");
        }
        this.defaultDataSourceName = defaultDataSourceName;
        this.defaultClient = Objects.requireNonNull(defaultClient, "Default MongoClient must not be null");
        Objects.requireNonNull(databaseNames, "Database names map must not be null");

        Map<String, MongoClient> clientMap = new LinkedHashMap<>();
        clientMap.put(defaultDataSourceName, defaultClient);

        List<MongoClient> managed = new ArrayList<>();
        Set<MongoClient> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        if (namedClients != null) {
            namedClients.forEach((name, client) -> {
                if (name == null || name.isBlank()) {
                    throw new IllegalArgumentException("MongoDB data source name must not be blank");
                }
                if (defaultDataSourceName.equals(name)) {
                    throw new IllegalArgumentException("MongoDB data source name '" + name
                        + "' is reserved for the default client");
                }
                MongoClient namedClient = Objects.requireNonNull(client,
                    "MongoClient for data source '" + name + "' must not be null");
                clientMap.put(name, namedClient);
                if (namedClient != defaultClient && seen.add(namedClient)) {
                    managed.add(namedClient);
                }
            });
        }

        this.clients = Collections.unmodifiableMap(clientMap);
        this.databaseNames = Collections.unmodifiableMap(new LinkedHashMap<>(databaseNames));
        this.managedClients = List.copyOf(managed);
    }

    public MongoClient getCurrentClient() {
        String selectedDataSourceName = MongoDataSourceContextHolder.getCurrentDataSource();
        if (selectedDataSourceName == null || selectedDataSourceName.isBlank()) {
            selectedDataSourceName = defaultDataSourceName;
        }

        MongoClient client = clients.get(selectedDataSourceName);
        if (client == null) {
            throw new IllegalStateException("MongoDB data source '" + selectedDataSourceName + "' is not configured");
        }
        return client;
    }

    public String getCurrentDatabaseName() {
        String selectedDataSourceName = MongoDataSourceContextHolder.getCurrentDataSource();
        if (selectedDataSourceName == null || selectedDataSourceName.isBlank()) {
            selectedDataSourceName = defaultDataSourceName;
        }

        String databaseName = databaseNames.get(selectedDataSourceName);
        if (databaseName == null) {
            throw new IllegalStateException("MongoDB database name for '" + selectedDataSourceName + "' is not configured");
        }
        return databaseName;
    }

    /**
     * Closes the named clients created by Lodsve. The default client remains container-owned.
     */
    public void close() {
        closeManagedClients();
    }

    private void closeManagedClients() {
        if (!managedClientsClosed.compareAndSet(false, true)) {
            return;
        }

        RuntimeException closeFailure = null;
        for (MongoClient client : managedClients) {
            try {
                client.close();
            } catch (RuntimeException exception) {
                closeFailure = merge(closeFailure, exception);
            }
        }

        if (closeFailure != null) {
            throw closeFailure;
        }
    }

    private RuntimeException merge(RuntimeException current, RuntimeException next) {
        if (current == null) {
            return next;
        }
        current.addSuppressed(next);
        return current;
    }
}
