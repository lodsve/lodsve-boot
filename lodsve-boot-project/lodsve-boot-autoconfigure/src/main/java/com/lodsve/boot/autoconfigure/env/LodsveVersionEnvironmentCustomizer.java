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

import com.lodsve.boot.LodsveBootVersion;

import java.util.Collections;
import java.util.Map;

/**
 * 加载环境变量后，需要进行一些额外处理.
 *
 * @author sunhao(hulk)
 */
public class LodsveVersionEnvironmentCustomizer implements EnvironmentCustomizer {
    private static final String G_COMMON_VERSION = "lodsve.version";

    @Override
    public Map<String, Object> customizer() {
        return Collections.singletonMap(G_COMMON_VERSION, LodsveBootVersion.getVersion());
    }
}
