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

import java.util.Collections;
import java.util.Map;

/**
 * spring bean可以复写的开关.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class BeanAllowOverrideEnvironmentCustomizer implements EnvironmentCustomizer {
    private static final String ENABLE_ALLOW_BEAN_DEFINITION_OVERRIDING = "spring.main.allow-bean-definition-overriding";

    @Override
    public Map<String, Object> customizer() {
        return Collections.singletonMap(ENABLE_ALLOW_BEAN_DEFINITION_OVERRIDING, true);
    }
}
