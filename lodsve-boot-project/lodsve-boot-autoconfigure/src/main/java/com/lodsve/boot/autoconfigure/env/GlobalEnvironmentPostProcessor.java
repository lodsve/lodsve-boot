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
package com.lodsve.boot.autoconfigure.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;
import java.util.Properties;

/**
 * 全局的配置定制.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class GlobalEnvironmentPostProcessor implements EnvironmentPostProcessor {
    public static final String LODSVE_PROPERTIES_SOURCE = "lodsveBootConfigurationProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (null != environment.getPropertySources().get(LODSVE_PROPERTIES_SOURCE)) {
            return;
        }

        List<EnvironmentCustomizer> customizers = SpringFactoriesLoader.loadFactories(EnvironmentCustomizer.class, Thread.currentThread().getContextClassLoader());
        Properties env = new Properties();
        customizers.forEach(c -> env.putAll(c.customizer()));

        PropertiesPropertySource propertySource = new PropertiesPropertySource(LODSVE_PROPERTIES_SOURCE, env);
        environment.getPropertySources().addLast(propertySource);
    }
}
