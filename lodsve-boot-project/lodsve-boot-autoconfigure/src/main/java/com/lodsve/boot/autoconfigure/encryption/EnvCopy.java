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
package com.lodsve.boot.autoconfigure.encryption;

import com.lodsve.boot.autoconfigure.encryption.source.EncryptablePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Optional;

/**
 * Need a copy of the environment without the Enhanced property sources to avoid circular dependencies..
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class EnvCopy {
    private final StandardEnvironment copy;

    public EnvCopy(final ConfigurableEnvironment environment) {
        copy = new StandardEnvironment();
        Optional.of(environment.getPropertySources()).ifPresent(sources -> sources.forEach(ps -> {
            PropertySource<?> original = ps instanceof EncryptablePropertySource ? ((EncryptablePropertySource<?>) ps).getDelegate() : ps;
            copy.getPropertySources().addLast(original);
        }));
    }

    public ConfigurableEnvironment get() {
        return copy;
    }
}
