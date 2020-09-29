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
package com.lodsve.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * lodsve-boot所需要的内置参数.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class LodsveBootCustomizerEnvironment implements EnvironmentPostProcessor {
    private static final String LODSVE_BOOT_PROPERTIES_SOURCE = "lodsveBootConfigurationProperties";
    private static final String LODSVE_BOOT_VERSION = "lodsve-boot.version";
    private static final String LODSVE_BOOT_AUTHOR = "lodsve-boot.author";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (null != environment.getPropertySources().get(LODSVE_BOOT_PROPERTIES_SOURCE)) {
            return;
        }

        Properties lodsveBootProperties = getLodsveBootProperties();

        PropertiesPropertySource propertySource = new PropertiesPropertySource(LODSVE_BOOT_PROPERTIES_SOURCE, lodsveBootProperties);
        environment.getPropertySources().addLast(propertySource);
    }

    private Properties getLodsveBootProperties() {
        Properties properties = new Properties();
        // 版本号
        properties.put(LODSVE_BOOT_VERSION, LodsveBootVersion.getVersion());
        // 作者
        properties.put(LODSVE_BOOT_AUTHOR, LodsveBootVersion.getBuilter());
        return properties;
    }
}
