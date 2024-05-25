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
package com.lodsve.boot.autoconfigure.webmvc;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * web mvc 配置.
 *
 * @author Hulk Sun
 */
@ConfigurationProperties(prefix = "lodsve.web-mvc")
@Data
public class WebMvcProperties {
    /**
     * Debug Config
     */
    private DebugConfig debug = new DebugConfig();
    /**
     * rest config
     */
    private RestConfig rest = new RestConfig();

    @Data
    public static class DebugConfig {
        /**
         * 需要忽略的url
         */
        private List<String> excludeUrl = Lists.newArrayList();
        /**
         * 需要忽略的ip/address
         */
        private List<String> excludeAddress = Lists.newArrayList();
    }

    @Data
    public static class RestConfig {
        /**
         * 连接超时时间，单位：毫秒
         */
        private int connectTimeout = 15000;
        /**
         * 读超时时间，单位：毫秒
         */
        private int readTimeout = 15000;
    }
}
