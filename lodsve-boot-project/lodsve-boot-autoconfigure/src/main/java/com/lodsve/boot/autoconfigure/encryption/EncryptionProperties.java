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
package com.lodsve.boot.autoconfigure.encryption;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 解密配置文件的配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Data
@ConfigurationProperties(prefix = "lodsve.encryption")
public class EncryptionProperties {
    /**
     * base64解密
     */
    private Base64 base64 = new Base64();

    @Data
    public static class Base64 {
        /**
         * 加密字符串的前缀
         */
        private String prefix = "BASE64(";
        /**
         * 加密字符串的后缀
         */
        private String suffix = ")";
    }
}
