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

/**
 * 解密.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public interface EncryptablePropertyResolver {

    /**
     * 允许被解密
     *
     * @param value 密文字符串
     * @return 是否被允许
     */
    boolean allowResolve(String value);

    /**
     * Returns the unencrypted version of the value provided free on any prefixes/suffixes or any other metadata
     * surrounding the encrypted value. Or the actual same String if no encryption was detected.
     *
     * @param value the property value
     * @return either the same value if the value is not encrypted, or the decrypted version.
     */
    String resolvePropertyValue(String value);
}
