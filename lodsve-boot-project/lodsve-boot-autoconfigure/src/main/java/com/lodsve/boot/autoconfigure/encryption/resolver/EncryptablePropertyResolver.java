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

/**
 * 解密.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public interface EncryptablePropertyResolver {

    /**
     * 允许被解密
     *
     * @param value 密文字符串
     * @return 是否被允许
     */
    boolean allowResolve(String value);

    /**
     * Returns the unencrypted version of the value provided free on any prefixes/suffixes or any other metadata
     * surrounding the encrypted value. Or the actual same String if no encryption was detected.
     *
     * @param value the property value
     * @return either the same value if the value is not encrypted, or the decrypted version.
     */
    String resolvePropertyValue(String value);
}
