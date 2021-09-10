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
