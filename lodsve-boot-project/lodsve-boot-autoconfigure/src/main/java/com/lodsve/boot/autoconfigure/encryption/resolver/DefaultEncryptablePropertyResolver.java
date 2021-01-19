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
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

/**
 * 默认的解密类--Base64.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class DefaultEncryptablePropertyResolver extends BaseEncryptablePropertyResolver {

    public DefaultEncryptablePropertyResolver(Environment environment, String prefix, String suffix) {
        super(environment, prefix, suffix);
    }

    @Override
    protected String decrypt(String property) {
        return StringUtils.toEncodedString(Base64Utils.decodeFromString(property), StandardCharsets.UTF_8);
    }
}
