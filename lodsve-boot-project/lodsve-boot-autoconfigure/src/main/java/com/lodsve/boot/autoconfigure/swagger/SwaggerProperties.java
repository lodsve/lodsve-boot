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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * swagger配置.
 *
 * @author Hulk Sun
 */
@ConfigurationProperties(prefix = "lodsve.swagger")
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<GlobalParameter> getGlobalParameters() {
        return globalParameters;
    }

    public void setGlobalParameters(List<GlobalParameter> globalParameters) {
        this.globalParameters = globalParameters;
    }

    public AuthConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthConfig auth) {
        this.auth = auth;
    }

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

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
         * 参数类型
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }

    public static class AuthConfig {
        /**
         * 是否启用认证
         */
        private boolean enabled;
        /**
         * 认证的key名称
         */
        private String key;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
