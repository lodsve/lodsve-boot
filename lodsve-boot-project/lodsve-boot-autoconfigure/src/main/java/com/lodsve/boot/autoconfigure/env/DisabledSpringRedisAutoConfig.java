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
