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

import com.lodsve.boot.autoconfigure.encryption.resolver.DefaultEncryptablePropertyResolver;
import com.lodsve.boot.autoconfigure.encryption.resolver.EncryptablePropertyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;

/**
 * 解密配置文件的配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@EnableConfigurationProperties(EncryptionProperties.class)
@Configuration
public class EncryptionAutoConfiguration {
    public static final Logger logger = LoggerFactory.getLogger(EncryptionAutoConfiguration.class);

    @Bean
    public EncryptablePropertyResolver defaultEncryptablePropertyResolver(ConfigurableEnvironment environment, EncryptionProperties properties) {
        EncryptionProperties.Base64 base64 = properties.getBase64();
        return new DefaultEncryptablePropertyResolver(environment, base64.getPrefix(), base64.getSuffix());
    }

    @Bean
    public EncryptablePropertiesBeanFactoryPostProcessor encryptablePropertiesBeanFactoryPostProcessor(ConfigurableEnvironment environment, List<EncryptablePropertyResolver> propertyResolvers) {
        return new EncryptablePropertiesBeanFactoryPostProcessor(environment, propertyResolvers);
    }
}
