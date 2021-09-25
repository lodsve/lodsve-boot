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

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 健康检查的两个端点（readiness、liveness）.
 *
 * @author Hulk Sun
 */
public class HealthProbesEnvironmentCustomizer implements EnvironmentCustomizer {
    private static final String ENABLE_HEALTH_PROBES = "management.endpoint.health.probes.enabled";
    private static final String ENABLE_HEALTH_PROBES_READINESS = "management.health.readinessstate.enabled";
    private static final String ENABLE_HEALTH_PROBES_LIVENESS = "management.health.liveliness.enabled";

    @Override
    public Map<String, Object> customizer() {
        Map<String, Object> env = Maps.newHashMap();
        env.put(ENABLE_HEALTH_PROBES, true);
        env.put(ENABLE_HEALTH_PROBES_READINESS, true);
        env.put(ENABLE_HEALTH_PROBES_LIVENESS, true);

        return env;
    }
}
