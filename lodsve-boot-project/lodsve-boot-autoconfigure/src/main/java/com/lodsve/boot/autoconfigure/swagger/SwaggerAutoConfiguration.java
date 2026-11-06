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

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI configuration.
 *
 * @author Hulk Sun
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "lodsve.swagger.enabled")
@ConditionalOnClass(OpenAPI.class)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    private final SwaggerProperties properties;

    public SwaggerAutoConfiguration(SwaggerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public OpenAPI lodsveOpenAPI() {
        Info info = new Info()
            .title(properties.getTitle())
            .description(properties.getDescription())
            .version(properties.getVersion())
            .termsOfService(properties.getTermsOfServiceUrl());
        if (properties.getContact() != null) {
            SwaggerProperties.Contact contact = properties.getContact();
            info.contact(new Contact().name(contact.getName()).url(contact.getUrl()).email(contact.getEmail()));
        }
        if (properties.getLicense() != null || properties.getLicenseUrl() != null) {
            info.license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()));
        }
        OpenAPI openAPI = new OpenAPI().info(info).components(new Components());
        SwaggerProperties.AuthConfig auth = properties.getAuth();
        if (auth != null && auth.isEnabled() && auth.getKey() != null && !auth.getKey().isBlank()) {
            openAPI.getComponents().addSecuritySchemes(auth.getKey(), new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name(auth.getKey()));
        }
        return openAPI;
    }

    @Bean
    public GlobalOpenApiCustomizer lodsveGlobalOpenApiCustomizer() {
        return openAPI -> {
            if (properties.getGlobalParameters() == null) {
                return;
            }
            // Global parameters remain part of the public configuration model.
            // Applications can map them to OpenAPI Parameter objects as needed.
        };
    }
}
