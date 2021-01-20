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

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.springframework.core.env.Environment;

/**
 * 基于Jasypt的解密方案.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class JasyptEncryptablePropertyResolver extends BaseEncryptablePropertyResolver {
    private final PBEStringEncryptor encryptor;

    public JasyptEncryptablePropertyResolver(Environment environment, String prefix, String suffix, PBEStringEncryptor encryptor) {
        super(environment, prefix, suffix);
        this.encryptor = encryptor;
    }

    @Override
    protected String decrypt(String property) {
        return encryptor.decrypt(property);
    }
}
