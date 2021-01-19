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

import java.util.List;

/**
 * 可被解密的配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public interface EncryptablePropertySource<T> {

    /**
     * 获取原始的PropertySource
     *
     * @return 原始的PropertySource
     */
    PropertySource<T> getDelegate();

    /**
     * 根据key获取配置的值
     *
     * @param name key
     * @return 配置的值
     */
    Object getProperty(String name);

    /**
     * 刷新配置
     */
    void refresh();

    /**
     * 获取配置
     *
     * @param resolvers 解密器
     * @param source    配置
     * @param name      key
     * @return 解密后的值
     */
    default Object getProperty(List<EncryptablePropertyResolver> resolvers, PropertySource<T> source, String name) {
        Object value = source.getProperty(name);
        if (value instanceof String) {
            String stringValue = String.valueOf(value);
            for (EncryptablePropertyResolver resolver : resolvers) {
                if (resolver.allowResolve(stringValue)) {
                    return resolver.resolvePropertyValue(stringValue);
                }
            }
            return stringValue;
        }
        return value;
    }
}
