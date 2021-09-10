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
package com.lodsve.boot.autoconfigure.encryption.resolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * 解密公共部分.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public abstract class BaseEncryptablePropertyResolver implements EncryptablePropertyResolver {
    private final Environment environment;
    private final String prefix;
    private final String suffix;

    protected BaseEncryptablePropertyResolver(Environment environment, String prefix, String suffix) {
        this.environment = environment;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public boolean allowResolve(String value) {
        return isEncrypted(value);
    }

    @Override
    public String resolvePropertyValue(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        if (!isEncrypted(value)) {
            return value;
        }

        value = environment.resolvePlaceholders(value);
        value = unwrapEncryptedValue(value);

        return decrypt(value);
    }

    /**
     * 解密
     *
     * @param property 加密后的字符串
     * @return 明文
     */
    protected abstract String decrypt(String property);

    private boolean isEncrypted(String property) {
        if (property == null) {
            return false;
        }
        final String trimmedValue = property.trim();
        return (trimmedValue.startsWith(prefix) && trimmedValue.endsWith(suffix));
    }

    private String unwrapEncryptedValue(String property) {
        return property.substring(prefix.length(), (property.length() - suffix.length()));
    }
}
