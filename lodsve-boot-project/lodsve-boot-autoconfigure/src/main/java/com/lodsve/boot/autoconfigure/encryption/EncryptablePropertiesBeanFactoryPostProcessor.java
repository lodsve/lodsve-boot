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

import com.lodsve.boot.autoconfigure.encryption.resolver.EncryptablePropertyResolver;
import com.lodsve.boot.autoconfigure.encryption.source.EncryptablePropertySource;
import com.lodsve.boot.autoconfigure.encryption.source.aop.EncryptablePropertySourceMethodInterceptor;
import com.lodsve.boot.autoconfigure.encryption.source.wrapper.EncryptableEnumerablePropertySourceWrapper;
import com.lodsve.boot.autoconfigure.encryption.source.wrapper.EncryptableMapPropertySourceWrapper;
import com.lodsve.boot.autoconfigure.encryption.source.wrapper.EncryptablePropertySourceWrapper;
import com.lodsve.boot.autoconfigure.encryption.source.wrapper.EncryptableSystemEnvironmentPropertySourceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * 配置开始解密.
 *
 * @author Hulk Sun
 */
public class EncryptablePropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(EncryptablePropertiesBeanFactoryPostProcessor.class);
    private final ConfigurableEnvironment environment;
    private final List<EncryptablePropertyResolver> propertyResolvers;

    public EncryptablePropertiesBeanFactoryPostProcessor(ConfigurableEnvironment environment, List<EncryptablePropertyResolver> propertyResolvers) {
        this.environment = environment;
        this.propertyResolvers = propertyResolvers;
    }

    @Override
    public void postProcessBeanFactory(@Nonnull ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        logger.info("Post-processing PropertySource instances");
        MutablePropertySources propSources = environment.getPropertySources();

        StreamSupport.stream(propSources.spliterator(), false)
            .filter(ps -> !(ps instanceof EncryptablePropertySource<?>))
            .map(this::makeEncryptable)
            .collect(toList())
            .forEach(ps -> propSources.replace(ps.getName(), ps));
    }

    public <T> PropertySource<T> makeEncryptable(PropertySource<T> propertySource) {
        PropertySource<?> convertedPropertySource = convertPropertySource(propertySource);
        boolean sameDelegate = AopUtils.isAopProxy(convertedPropertySource)
            ? AopProxyUtils.getSingletonTarget(convertedPropertySource) == propertySource
            : convertedPropertySource instanceof EncryptablePropertySource<?> encryptable && encryptable.getDelegate() == propertySource;
        Assert.isTrue(sameDelegate, "Encrypted property source must preserve its delegate");
        // 包装器从同一个 delegate 获取 source，保留类型 T；仅在此处处理泛型擦除。
        @SuppressWarnings("unchecked")
        PropertySource<T> encryptablePropertySource = (PropertySource<T>) convertedPropertySource;
        logger.info("Converting PropertySource {} [{}] to {}", propertySource.getName(), propertySource.getClass().getName(),
            AopUtils.isAopProxy(encryptablePropertySource) ? "AOP Proxy" : encryptablePropertySource.getClass().getSimpleName());
        return encryptablePropertySource;
    }

    private PropertySource<?> convertPropertySource(PropertySource<?> propertySource) {
        PropertySource<?> encryptablePropertySource;
        if (needsProxyAnyway(propertySource)
            && !(propertySource instanceof CommandLinePropertySource<?>)
            && !Modifier.isFinal(propertySource.getClass().getModifiers())) {
            encryptablePropertySource = proxyPropertySource(propertySource);
        } else if (propertySource instanceof SystemEnvironmentPropertySource systemEnvironmentPropertySource) {
            encryptablePropertySource = new EncryptableSystemEnvironmentPropertySourceWrapper(systemEnvironmentPropertySource, propertyResolvers);
        } else if (propertySource instanceof MapPropertySource mapPropertySource) {
            encryptablePropertySource = new EncryptableMapPropertySourceWrapper(mapPropertySource, propertyResolvers);
        } else if (propertySource instanceof EnumerablePropertySource<?> enumerablePropertySource) {
            encryptablePropertySource = new EncryptableEnumerablePropertySourceWrapper<>(enumerablePropertySource, propertyResolvers);
        } else {
            encryptablePropertySource = new EncryptablePropertySourceWrapper<>(propertySource, propertyResolvers);
        }
        return encryptablePropertySource;
    }

    private PropertySource<?> proxyPropertySource(PropertySource<?> propertySource) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(propertySource.getClass());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addInterface(EncryptablePropertySource.class);
        proxyFactory.setTarget(propertySource);
        proxyFactory.addAdvice(new EncryptablePropertySourceMethodInterceptor<>(propertySource, propertyResolvers));
        return propertySource.getClass().cast(proxyFactory.getProxy());
    }

    private <T> boolean needsProxyAnyway(PropertySource<T> propertySource) {
        String className = propertySource.getClass().getName();
        return Arrays.asList(
            "org.springframework.boot.context.config.ConfigFileApplicationListener$ConfigurationPropertySources",
            "org.springframework.boot.context.properties.source.ConfigurationPropertySourcesPropertySource"
        ).contains(className);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
