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

import java.util.Collections;
import java.util.Map;

/**
 * spring bean可以复写的开关.
 *
 * @author Hulk Sun
 */
public class BeanAllowOverrideEnvironmentCustomizer implements EnvironmentCustomizer {
    private static final String ENABLE_ALLOW_BEAN_DEFINITION_OVERRIDING = "spring.main.allow-bean-definition-overriding";

    @Override
    public Map<String, Object> customizer() {
        return Collections.singletonMap(ENABLE_ALLOW_BEAN_DEFINITION_OVERRIDING, true);
    }
}
