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
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Objects;

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
        return this::customizeGlobalParameters;
    }

    private void customizeGlobalParameters(OpenAPI openAPI) {
        if (properties.getGlobalParameters() != null
            && !properties.getGlobalParameters().isEmpty()
            && openAPI.getPaths() != null) {
            openAPI.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation ->
                properties.getGlobalParameters().stream()
                    .filter(Objects::nonNull)
                    .map(this::toOpenApiParameter)
                    .filter(Objects::nonNull)
                    .filter(parameter -> operation.getParameters() == null
                        || operation.getParameters().stream().noneMatch(existing ->
                        Objects.equals(existing.getName(), parameter.getName())
                            && Objects.equals(existing.getIn(), parameter.getIn())))
                    .forEach(operation::addParametersItem)));
        }
    }

    private Parameter toOpenApiParameter(SwaggerProperties.GlobalParameter globalParameter) {
        if (globalParameter.getName() == null || globalParameter.getName().isBlank()) {
            return null;
        }
        String scope = globalParameter.getScope() == null
            ? "query" : globalParameter.getScope().toLowerCase(Locale.ROOT);
        if (!"query".equals(scope) && !"header".equals(scope)
            && !"path".equals(scope) && !"cookie".equals(scope)) {
            return null;
        }
        Parameter parameter = new Parameter();
        parameter.setName(globalParameter.getName());
        parameter.setIn(scope);
        parameter.setDescription(globalParameter.getDescription());
        parameter.setRequired(globalParameter.isRequired() || "path".equals(scope));
        parameter.setSchema(toSchema(globalParameter.getType()));
        return parameter;
    }

    private Schema<?> toSchema(String type) {
        if (type == null) {
            return new StringSchema();
        }
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "integer" -> new IntegerSchema();
            case "number" -> new NumberSchema();
            case "boolean" -> new BooleanSchema();
            case "object" -> new ObjectSchema();
            default -> new StringSchema();
        };
    }
}
