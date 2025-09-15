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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.lang.annotation.Annotation;

/**
 * 解密配置文件的配置.
 *
 * @author Hulk Sun
 */
@ConfigurationProperties(prefix = "lodsve.encryption")
public class EncryptionProperties {
    public static EncryptionProperties bindConfigProps(ConfigurableEnvironment environment) {
        BindHandler handler = new IgnoreErrorsBindHandler(BindHandler.DEFAULT);
        MutablePropertySources propertySources = environment.getPropertySources();
        Binder binder = new Binder(ConfigurationPropertySources.from(propertySources), new PropertySourcesPlaceholdersResolver(propertySources),
            ApplicationConversionService.getSharedInstance());
        EncryptionProperties config = new EncryptionProperties();

        ResolvableType type = ResolvableType.forClass(EncryptionProperties.class);
        Annotation annotation = AnnotationUtils.findAnnotation(EncryptionProperties.class, ConfigurationProperties.class);
        Annotation[] annotations = new Annotation[]{annotation};
        Bindable<?> target = Bindable.of(type).withExistingValue(config).withAnnotations(annotations);

        binder.bind("lodsve.encryption", target, handler);
        return config;
    }

    /**
     * 是否启用
     */
    private boolean enabled = false;
    /**
     * base64解密
     */
    private Base64 base64 = new Base64();
    /**
     * jasypt解密
     */
    private Jasypt jasypt = new Jasypt();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Base64 getBase64() {
        return base64;
    }

    public void setBase64(Base64 base64) {
        this.base64 = base64;
    }

    public Jasypt getJasypt() {
        return jasypt;
    }

    public void setJasypt(Jasypt jasypt) {
        this.jasypt = jasypt;
    }

    public static class Base64 {
        /**
         * 加密字符串的前缀
         */
        private String prefix = "BASE64(";
        /**
         * 加密字符串的后缀
         */
        private String suffix = ")";

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }

    public static class Jasypt {
        /**
         * 加密字符串的前缀
         */
        private String prefix = "ENC(";
        /**
         * 加密字符串的后缀
         */
        private String suffix = ")";
        /**
         * 盐
         */
        private String password;
        /**
         * 解密算法
         */
        private String algorithm = "PBEWithMD5AndDES";
        /**
         * 获取签名密钥的哈希迭代次数
         */
        private String keyObtentionIterations = "1000";
        /**
         * 要创建的加密器池的大小
         */
        private String poolSize = "1";
        /**
         * 加密程序将用于获取加密算法的{@link java.security.Provider}实现的名称。
         */
        private String providerName = null;
        /**
         * 加密程序将使用{@link java.security.Provider}实现的类名称来获取加密算法。 默认值为{@code null}
         */
        private String providerClassName = null;
        /**
         * 加密器要使用的{@link org.jasypt.salt.SaltGenerator}实现。 默认值为{@code "org.jasypt.salt.RandomSaltGenerator"}
         */
        private String saltGeneratorClassname = "org.jasypt.salt.RandomSaltGenerator";
        /**
         * 加密器要使用的{@link org.jasypt.iv.IvGenerator}实现。 默认值为{@code "org.jasypt.iv.NoIvGenerator"}.
         */
        private String ivGeneratorClassname = "org.jasypt.iv.NoIvGenerator";
        /**
         * 指定将String输出编码的形式.{@code "base64"} or {@code "hexadecimal"}.默认值为{@code "base64"}
         */
        private String stringOutputType = "base64";

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getKeyObtentionIterations() {
            return keyObtentionIterations;
        }

        public void setKeyObtentionIterations(String keyObtentionIterations) {
            this.keyObtentionIterations = keyObtentionIterations;
        }

        public String getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(String poolSize) {
            this.poolSize = poolSize;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public String getProviderClassName() {
            return providerClassName;
        }

        public void setProviderClassName(String providerClassName) {
            this.providerClassName = providerClassName;
        }

        public String getSaltGeneratorClassname() {
            return saltGeneratorClassname;
        }

        public void setSaltGeneratorClassname(String saltGeneratorClassname) {
            this.saltGeneratorClassname = saltGeneratorClassname;
        }

        public String getIvGeneratorClassname() {
            return ivGeneratorClassname;
        }

        public void setIvGeneratorClassname(String ivGeneratorClassname) {
            this.ivGeneratorClassname = ivGeneratorClassname;
        }

        public String getStringOutputType() {
            return stringOutputType;
        }

        public void setStringOutputType(String stringOutputType) {
            this.stringOutputType = stringOutputType;
        }
    }
}
