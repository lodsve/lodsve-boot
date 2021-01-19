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
import org.springframework.core.env.*;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * 配置开始解密.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
