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

import com.lodsve.boot.autoconfigure.encryption.EncryptionProperties.Jasypt;
import com.lodsve.boot.autoconfigure.encryption.resolver.DefaultEncryptablePropertyResolver;
import com.lodsve.boot.autoconfigure.encryption.resolver.EncryptablePropertyResolver;
import com.lodsve.boot.autoconfigure.encryption.resolver.JasyptEncryptablePropertyResolver;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

    @Bean
    public EnvCopy envCopy(final ConfigurableEnvironment environment) {
        return new EnvCopy(environment);
    }

    @Bean
    public EncryptablePropertyResolver defaultEncryptablePropertyResolver(ConfigurableEnvironment environment, EnvCopy envCopy) {
        EncryptionProperties properties = EncryptionProperties.bindConfigProps(envCopy.get());
        EncryptionProperties.Base64 base64 = properties.getBase64();
        return new DefaultEncryptablePropertyResolver(environment, base64.getPrefix(), base64.getSuffix());
    }

    @Bean
    public EncryptablePropertiesBeanFactoryPostProcessor encryptablePropertiesBeanFactoryPostProcessor(ConfigurableEnvironment environment, List<EncryptablePropertyResolver> propertyResolvers) {
        return new EncryptablePropertiesBeanFactoryPostProcessor(environment, propertyResolvers);
    }

    /**
     * 基于Jasypt的解密配置
     */
    @Configuration
    @ConditionalOnClass(PooledPBEStringEncryptor.class)
    public static class JasyptEncryptablePropertyResolverConfiguration {
        private EncryptionProperties properties;

        public EncryptionProperties init(EnvCopy envCopy) {
            if (null == properties) {
                properties = EncryptionProperties.bindConfigProps(envCopy.get());
            }

            return properties;
        }

        @Bean
        public PBEStringEncryptor encryptor(ObjectProvider<EnvCopy> envCopy) {
            EncryptionProperties properties = init(envCopy.getIfAvailable());
            Jasypt jasypt = properties.getJasypt();

            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword(jasypt.getPassword());
            config.setAlgorithm(jasypt.getAlgorithm());
            config.setKeyObtentionIterations(jasypt.getKeyObtentionIterations());
            config.setPoolSize(jasypt.getPoolSize());
            config.setProviderName(jasypt.getProviderName());
            config.setProviderClassName(jasypt.getProviderClassName());
            config.setSaltGeneratorClassName(jasypt.getSaltGeneratorClassname());
            config.setIvGeneratorClassName(jasypt.getIvGeneratorClassname());
            config.setStringOutputType(jasypt.getStringOutputType());
            encryptor.setConfig(config);

            return encryptor;
        }

        @Bean
        public EncryptablePropertyResolver jasyptEncryptablePropertyResolver(ObjectProvider<EnvCopy> envCopy, ConfigurableEnvironment environment, PBEStringEncryptor encryptor) {
            EncryptionProperties properties = init(envCopy.getIfAvailable());
            Jasypt jasypt = properties.getJasypt();
            return new JasyptEncryptablePropertyResolver(environment, jasypt.getPrefix(), jasypt.getSuffix(), encryptor);
        }
    }
}
