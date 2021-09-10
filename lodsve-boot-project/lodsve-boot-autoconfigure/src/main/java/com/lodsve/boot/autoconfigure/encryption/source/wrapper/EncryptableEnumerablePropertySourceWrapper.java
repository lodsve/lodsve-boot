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
package com.lodsve.boot.autoconfigure.encryption.source.wrapper;

import com.lodsve.boot.autoconfigure.encryption.resolver.EncryptablePropertyResolver;
import com.lodsve.boot.autoconfigure.encryption.source.CachingDelegateEncryptablePropertySource;
import com.lodsve.boot.autoconfigure.encryption.source.EncryptablePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.List;

/**
 * 可枚举的.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class EncryptableEnumerablePropertySourceWrapper<T> extends EnumerablePropertySource<T> implements EncryptablePropertySource<T> {
    private final EncryptablePropertySource<T> delegate;

    public EncryptableEnumerablePropertySourceWrapper(EnumerablePropertySource<T> delegate, List<EncryptablePropertyResolver> resolvers) {
        super(delegate.getName(), delegate.getSource());
        this.delegate = new CachingDelegateEncryptablePropertySource<>(delegate, resolvers);
    }

    @Override
    public Object getProperty(String name) {
        return delegate.getProperty(name);
    }

    @Override
    public void refresh() {
        delegate.refresh();
    }

    @Override
    public PropertySource<T> getDelegate() {
        return delegate.getDelegate();
    }

    @Override
    public String[] getPropertyNames() {
        return ((EnumerablePropertySource) delegate.getDelegate()).getPropertyNames();
    }
}
