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
package com.lodsve.boot.autoconfigure.encryption.source.wrapper;

import com.lodsve.boot.autoconfigure.encryption.resolver.EncryptablePropertyResolver;
import com.lodsve.boot.autoconfigure.encryption.source.CachingDelegateEncryptablePropertySource;
import com.lodsve.boot.autoconfigure.encryption.source.EncryptablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.List;

/**
 * properties.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class EncryptablePropertySourceWrapper<T> extends PropertySource<T> implements EncryptablePropertySource<T> {
    private final EncryptablePropertySource<T> encryptableDelegate;

    public EncryptablePropertySourceWrapper(PropertySource<T> delegate, List<EncryptablePropertyResolver> resolvers) {
        super(delegate.getName(), delegate.getSource());
        encryptableDelegate = new CachingDelegateEncryptablePropertySource<>(delegate, resolvers);
    }

    @Override
    public void refresh() {
        encryptableDelegate.refresh();
    }

    @Override
    public Object getProperty(String name) {
        return encryptableDelegate.getProperty(name);
    }

    @Override
    public PropertySource<T> getDelegate() {
        return encryptableDelegate.getDelegate();
    }
}
