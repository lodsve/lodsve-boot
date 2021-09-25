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
package com.lodsve.boot.autoconfigure.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * 配置.
 *
 * @author Hulk Sun
 */
@ConfigurationProperties("lodsve.core")
public class CoreProperties {
    /**
     * i18n文件目录
     */
    private Set<String> i18nFolders;

    public Set<String> getI18nFolders() {
        return i18nFolders;
    }

    public void setI18nFolders(Set<String> i18nFolders) {
        this.i18nFolders = i18nFolders;
    }
}
