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
