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
