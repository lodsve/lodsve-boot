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
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EncryptablePropertiesBeanFactoryPostProcessorTest {
    private final EncryptablePropertyResolver resolver = new EncryptablePropertyResolver() {
        @Override
        public boolean allowResolve(String value) {
            return value.startsWith("ENC(") && value.endsWith(")");
        }

        @Override
        public String resolvePropertyValue(String value) {
            return value.substring(4, value.length() - 1);
        }
    };

    @Test
    public void preservesMapSourceAndRefreshesCachedValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("secret", "ENC(first)");
        values.put("number", 21);
        MapPropertySource original = new MapPropertySource("map", values);
        PropertySource<Map<String, Object>> wrapped = processor(new StandardEnvironment()).makeEncryptable(original);
        Assert.assertTrue(wrapped instanceof MapPropertySource);
        Assert.assertSame(values, wrapped.getSource());
        Assert.assertSame(original, ((EncryptablePropertySource<?>) wrapped).getDelegate());
        Assert.assertEquals("first", wrapped.getProperty("secret"));
        Assert.assertEquals(21, wrapped.getProperty("number"));
        values.put("secret", "ENC(second)");
        Assert.assertEquals("first", wrapped.getProperty("secret"));
        ((EncryptablePropertySource<?>) wrapped).refresh();
        Assert.assertEquals("second", wrapped.getProperty("secret"));
    }

    @Test
    public void preservesEnvironmentVariableNameResolution() {
        Map<String, Object> values = Collections.singletonMap("APP_SECRET", "ENC(environment)");
        SystemEnvironmentPropertySource original = new SystemEnvironmentPropertySource("environment", values);
        PropertySource<Map<String, Object>> wrapped = processor(new StandardEnvironment()).makeEncryptable(original);
        Assert.assertTrue(wrapped instanceof SystemEnvironmentPropertySource);
        Assert.assertSame(values, wrapped.getSource());
        Assert.assertTrue(wrapped.containsProperty("app.secret"));
        Assert.assertEquals("environment", wrapped.getProperty("app.secret"));
    }

    @Test
    public void preservesEnumerableSourceTypeAndNames() {
        EnumerablePropertySource<String> original = new EnumerablePropertySource<String>("enumerable", "ENC(enumerable)") {
            @Override
            public String[] getPropertyNames() {
                return new String[]{"secret"};
            }

            @Override
            public Object getProperty(String name) {
                return "secret".equals(name) ? getSource() : null;
            }
        };
        PropertySource<String> wrapped = processor(new StandardEnvironment()).makeEncryptable(original);
        Assert.assertSame(original.getSource(), wrapped.getSource());
        Assert.assertArrayEquals(original.getPropertyNames(), ((EnumerablePropertySource<?>) wrapped).getPropertyNames());
        Assert.assertEquals("enumerable", wrapped.getProperty("secret"));
        Assert.assertNull(wrapped.getProperty("missing"));
    }

    @Test
    public void preservesOrdinarySourceType() {
        PropertySource<String> original = new PropertySource<String>("ordinary", "ENC(ordinary)") {
            @Override
            public Object getProperty(String name) {
                return "secret".equals(name) ? getSource() : null;
            }
        };
        PropertySource<String> wrapped = processor(new StandardEnvironment()).makeEncryptable(original);
        Assert.assertSame(original.getSource(), wrapped.getSource());
        Assert.assertEquals("ordinary", wrapped.getProperty("secret"));
    }

    @Test
    public void wrapsCommandLineAndFinalPropertySources() {
        EncryptablePropertiesBeanFactoryPostProcessor processor = processor(new StandardEnvironment());
        PropertySource<?> commandLine = processor.makeEncryptable(new SimpleCommandLinePropertySource("--secret=ENC(command)"));
        Assert.assertEquals("command", commandLine.getProperty("secret"));
        Assert.assertFalse(AopUtils.isAopProxy(commandLine));
        PropertySource<?> finalSource = processor.makeEncryptable(new FinalMapPropertySource());
        Assert.assertEquals("final", finalSource.getProperty("secret"));
    }

    @Test
    public void preservesSpringBootConfigurationPropertySourceProxy() {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        environment.getPropertySources().addFirst(new MapPropertySource("settings", Collections.singletonMap("app.secret", "ENC(proxy)")));
        ConfigurationPropertySources.attach(environment);
        PropertySource<?> original = environment.getPropertySources().get("configurationProperties");
        processor(environment).postProcessBeanFactory(new DefaultListableBeanFactory());
        PropertySource<?> wrapped = environment.getPropertySources().get("configurationProperties");
        Assert.assertNotNull(original);
        Assert.assertNotNull(wrapped);
        Assert.assertTrue(AopUtils.isAopProxy(wrapped));
        Assert.assertSame(original.getSource(), wrapped.getSource());
        Assert.assertEquals("proxy", environment.getProperty("app.secret"));
        Assert.assertNotNull(ConfigurationPropertySources.get(environment));
    }

    private EncryptablePropertiesBeanFactoryPostProcessor processor(StandardEnvironment environment) {
        return new EncryptablePropertiesBeanFactoryPostProcessor(environment, Collections.singletonList(resolver));
    }

    private static final class FinalMapPropertySource extends MapPropertySource {
        FinalMapPropertySource() {
            super("final", Collections.singletonMap("secret", "ENC(final)"));
        }
    }
}
