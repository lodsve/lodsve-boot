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
package com.lodsve.boot.autoconfigure.encryption.source;

import com.lodsve.boot.autoconfigure.encryption.resolver.EncryptablePropertyResolver;
import org.springframework.core.env.PropertySource;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class CachingDelegateEncryptablePropertySource<T> extends PropertySource<T> implements EncryptablePropertySource<T> {
    private final PropertySource<T> delegate;
    private final List<EncryptablePropertyResolver> resolvers;
    private final Map<String, Object> cache;

    public CachingDelegateEncryptablePropertySource(PropertySource<T> delegate, List<EncryptablePropertyResolver> resolvers) {
        super(delegate.getName(), delegate.getSource());
        Assert.notNull(delegate, "PropertySource delegate cannot be null");
        Assert.notNull(resolvers, "EncryptablePropertyResolver cannot be null");
        this.delegate = delegate;
        this.resolvers = resolvers;
        this.cache = new HashMap<>();
    }

    @Override
    public PropertySource<T> getDelegate() {
        return delegate;
    }

    @Override
    public Object getProperty(String name) {
        // Can be called recursively, so, we cannot use computeIfAbsent.
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        synchronized (this) {
            if (!cache.containsKey(name)) {
                Object resolved = getProperty(resolvers, delegate, name);
                if (resolved != null) {
                    cache.put(name, resolved);
                }
            }
            return cache.get(name);
        }
    }

    @Override
    public void refresh() {
        cache.clear();
    }
}
