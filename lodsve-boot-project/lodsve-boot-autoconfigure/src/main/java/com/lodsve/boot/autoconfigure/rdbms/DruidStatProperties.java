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
package com.lodsve.boot.autoconfigure.rdbms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * druid监控配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Data
@ConfigurationProperties(prefix = "lodsve.rdbms.druid")
public class DruidStatProperties {
    private String[] aopPatterns;
    private StatViewServlet statViewServlet = new StatViewServlet();
    private WebStatFilter webStatFilter = new WebStatFilter();

    @Data
    public static class StatViewServlet {
        /**
         * Enable StatViewServlet, default false.
         */
        private boolean enabled;
        /**
         * 目前这个属性是废弃的，因为druid只支持设置为/druid/*
         */
        @Deprecated
        private String urlPattern;
        private String allow;
        private String deny;
        private String loginUsername;
        private String loginPassword;
        private String resetEnable;
    }

    @Data
    public static class WebStatFilter {
        /**
         * Enable WebStatFilter, default false.
         */
        private boolean enabled;
        private String urlPattern;
        private String exclusions;
        private String sessionStatMaxCount;
        private String sessionStatEnable;
        private String principalSessionName;
        private String principalCookieName;
        private String profileEnable;
    }
}
