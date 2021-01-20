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
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Data
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
     * base64解密
     */
    private Base64 base64 = new Base64();
    /**
     * jasypt解密
     */
    private Jasypt jasypt = new Jasypt();

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

    @Data
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
    }
}
