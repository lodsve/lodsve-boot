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
package com.lodsve.boot.autoconfigure.encryption.resolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

/**
 * 默认的解密类--Base64.
 *
 * @author Hulk Sun
 */
public class DefaultEncryptablePropertyResolver extends BaseEncryptablePropertyResolver {

    public DefaultEncryptablePropertyResolver(Environment environment, String prefix, String suffix) {
        super(environment, prefix, suffix);
    }

    @Override
    protected String decrypt(String property) {
        return StringUtils.toEncodedString(Base64Utils.decodeFromString(property), StandardCharsets.UTF_8);
    }
}
