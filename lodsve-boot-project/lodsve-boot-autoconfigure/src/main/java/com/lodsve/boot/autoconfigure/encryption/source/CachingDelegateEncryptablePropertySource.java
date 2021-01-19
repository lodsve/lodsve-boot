/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
