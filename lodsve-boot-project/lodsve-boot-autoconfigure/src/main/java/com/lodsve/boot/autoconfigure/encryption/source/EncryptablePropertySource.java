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
