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

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 健康检查的两个端点（readiness、liveness）.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
