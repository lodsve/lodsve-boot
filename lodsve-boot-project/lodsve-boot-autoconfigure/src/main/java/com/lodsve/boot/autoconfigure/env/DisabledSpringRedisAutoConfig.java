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

import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 禁用springboot的redis配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class DisabledSpringRedisAutoConfig implements EnvironmentCustomizer {
    private static final String DISABLED_AUTO_CONFIGURATION_EXCLUDE = "spring.autoconfigure.exclude";
    private static final String DYNAMIC_CONNECTION_FACTORY_CLASS_NAME = "com.lodsve.boot.redis.dynamic.DynamicLettuceConnectionFactory";

    @Override
    public Map<String, Object> customizer() {
        if (ClassUtils.isPresent(DYNAMIC_CONNECTION_FACTORY_CLASS_NAME, Thread.currentThread().getContextClassLoader())) {
            return Collections.singletonMap(DISABLED_AUTO_CONFIGURATION_EXCLUDE, "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration");
        }

        return Collections.emptyMap();
    }
}
