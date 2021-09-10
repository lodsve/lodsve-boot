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
package com.lodsve.boot.autoconfigure.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * swagger配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ConfigurationProperties(prefix = "lodsve.swagger")
@Data
public class SwaggerProperties {
    /**
     * 接口版本号
     */
    private String version;
    /**
     * 接口文档标题
     */
    private String title;
    /**
     * 接口文档描述
     */
    private String description;
    /**
     * 项目团队地址
     */
    private String termsOfServiceUrl;
    /**
     * 项目使用的许可证
     */
    private String license;
    /**
     * 许可证描述地址
     */
    private String licenseUrl;
    /**
     * 项目联系人
     */
    @NestedConfigurationProperty
    private Contact contact;
    /**
     * 全局变量
     */
    @NestedConfigurationProperty
    private List<GlobalParameter> globalParameters;
    /**
     * 认证配置，目前仅支持header传参
     */
    @NestedConfigurationProperty
    private AuthConfig auth;

    @Data
    public static class Contact {
        /**
         * 项目联系人
         */
        private String name;
        /**
         * 项目联系人主页
         */
        private String url;
        /**
         * 项目联系人邮箱
         */
        private String email;
    }

    @Data
    public static class GlobalParameter {
        /**
         * 参数名称
         */
        private String name;
        /**
         * 参数描述
         */
        private String description;
        /**
         * 参数类型<p/>
         * integer/string/boolean/number/object
         */
        private String type;
        /**
         * 参数位置[query/header/path/cookie/form/formData/body]
         */
        private String scope;
        /**
         * 是否必填
         */
        private boolean required;
    }

    @Data
    public static class AuthConfig {
        /**
         * 是否启用认证
         */
        private boolean enabled;
        /**
         * 认证的key名称
         */
        private String key;
    }
}
