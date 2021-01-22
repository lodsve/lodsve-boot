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