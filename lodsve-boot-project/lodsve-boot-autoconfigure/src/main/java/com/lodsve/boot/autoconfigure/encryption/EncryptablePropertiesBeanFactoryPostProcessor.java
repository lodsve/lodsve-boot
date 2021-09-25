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
            .filter(ps -> !(ps instanceof EncryptablePropertySource))
            .map(this::makeEncryptable)
            .collect(toList())
            .forEach(ps -> propSources.replace(ps.getName(), ps));
    }

    public <T> PropertySource<T> makeEncryptable(PropertySource<T> propertySource) {
        PropertySource<T> encryptablePropertySource = convertPropertySource(propertySource);
        logger.info("Converting PropertySource {} [{}] to {}", propertySource.getName(), propertySource.getClass().getName(),
            AopUtils.isAopProxy(encryptablePropertySource) ? "AOP Proxy" : encryptablePropertySource.getClass().getSimpleName());
        return encryptablePropertySource;
    }

    private <T> PropertySource<T> convertPropertySource(PropertySource<T> propertySource) {
        PropertySource<T> encryptablePropertySource;
        if (needsProxyAnyway(propertySource)) {
            encryptablePropertySource = proxyPropertySource(propertySource);
        } else if (propertySource instanceof SystemEnvironmentPropertySource) {
            encryptablePropertySource = (PropertySource<T>) new EncryptableSystemEnvironmentPropertySourceWrapper((SystemEnvironmentPropertySource) propertySource, propertyResolvers);
        } else if (propertySource instanceof MapPropertySource) {
            encryptablePropertySource = (PropertySource<T>) new EncryptableMapPropertySourceWrapper((MapPropertySource) propertySource, propertyResolvers);
        } else if (propertySource instanceof EnumerablePropertySource) {
            encryptablePropertySource = new EncryptableEnumerablePropertySourceWrapper<>((EnumerablePropertySource) propertySource, propertyResolvers);
        } else {
            encryptablePropertySource = new EncryptablePropertySourceWrapper<>(propertySource, propertyResolvers);
        }
        return encryptablePropertySource;
    }

    private <T> PropertySource<T> proxyPropertySource(PropertySource<T> propertySource) {
        //Silly Chris Beams for making CommandLinePropertySource getProperty and containsProperty methods final. Those methods
        //can't be proxied with CGLib because of it. So fallback to wrapper for Command Line Arguments only.
        if (CommandLinePropertySource.class.isAssignableFrom(propertySource.getClass())
            // Other PropertySource classes like org.springframework.boot.env.OriginTrackedMapPropertySource
            // are final classes as well
            || Modifier.isFinal(propertySource.getClass().getModifiers())) {
            return convertPropertySource(propertySource);
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(propertySource.getClass());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addInterface(EncryptablePropertySource.class);
        proxyFactory.setTarget(propertySource);
        proxyFactory.addAdvice(new EncryptablePropertySourceMethodInterceptor<>(propertySource, propertyResolvers));
        return (PropertySource<T>) proxyFactory.getProxy();
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
