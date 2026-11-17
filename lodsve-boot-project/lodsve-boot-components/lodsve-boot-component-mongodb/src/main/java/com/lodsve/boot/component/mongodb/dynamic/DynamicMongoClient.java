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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * Creates a public MongoClient proxy that delegates calls to the selected source.
 */
public final class DynamicMongoClient implements InvocationHandler {
    private final MongoClientRegistry clientRegistry;

    private DynamicMongoClient(MongoClientRegistry clientRegistry) {
        this.clientRegistry = Objects.requireNonNull(clientRegistry, "MongoClientRegistry must not be null");
    }

    public static MongoClient create(MongoClientRegistry clientRegistry) {
        DynamicMongoClient invocationHandler = new DynamicMongoClient(clientRegistry);
        return (MongoClient) Proxy.newProxyInstance(MongoClient.class.getClassLoader(),
            new Class<?>[]{MongoClient.class, RegistryHolder.class}, invocationHandler);
    }

    public MongoClientRegistry getRegistry() {
        return clientRegistry;
    }

    /**
     * Marker interface to access the registry from the proxy.
     */
    public interface RegistryHolder {
        MongoClientRegistry getRegistry();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return handleObjectMethod(proxy, method, args);
        }
        if ("getRegistry".equals(method.getName()) && method.getParameterCount() == 0) {
            return clientRegistry;
        }
        if ("close".equals(method.getName()) && method.getParameterCount() == 0) {
            clientRegistry.close();
            return null;
        }

        try {
            return method.invoke(clientRegistry.getCurrentClient(), args);
        } catch (InvocationTargetException exception) {
            throw exception.getTargetException();
        }
    }

    private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
        return switch (method.getName()) {
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" -> "DynamicMongoClient";
            default -> throw new IllegalStateException("Unsupported MongoClient proxy method: " + method.getName());
        };
    }
}
