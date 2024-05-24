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

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.lodsve.boot.component.webmvc.convert.EnumCodeConverterFactory;
import com.lodsve.boot.component.webmvc.debug.DebugRequestAspect;
import com.lodsve.boot.component.webmvc.resolver.WebInput;
import com.lodsve.boot.component.webmvc.resolver.WebOutput;
import com.lodsve.boot.component.webmvc.response.LodsveBootExceptionHandler;
import com.lodsve.boot.component.webmvc.response.WebResultResponseWrapperHandler;
import com.lodsve.boot.component.webmvc.utils.RestUtils;
import com.lodsve.boot.json.JsonConverter;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;
import java.util.Objects;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * web mvc 配置.
 *
 * @author Hulk Sun
 */
@ConditionalOnWebApplication
@ConditionalOnClass(EnumCodeConverterFactory.class)
@EnableConfigurationProperties(WebMvcProperties.class)
@Configuration
public class WebMvcAutoConfiguration {
    @Bean
    public WebMvcConfigurer webMvcConfigurer(ObjectProvider<JsonConverter> jsonConverter) {
        return new LodsveWebMvcConfigurer(jsonConverter.getIfAvailable());
    }

    @Bean
    public WebResultResponseWrapperHandler responseHandler(ObjectProvider<JsonConverter> jsonConverter) {
        return new WebResultResponseWrapperHandler(jsonConverter.getIfAvailable());
    }

    @Bean
    public LodsveBootExceptionHandler exceptionHandler() {
        return new LodsveBootExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean({RestTemplate.class})
    public RestTemplate restTemplate(ClientHttpRequestFactory factory, HttpMessageConverters messageConverters) {
        RestTemplate restTemplate = new RestTemplate(messageConverters.getConverters());
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean({ClientHttpRequestFactory.class})
    public ClientHttpRequestFactory requestFactory(WebMvcProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getRest().getConnectTimeout());
        factory.setReadTimeout(properties.getRest().getReadTimeout());
        return factory;
    }

    @Bean
    public InitializingBean initRestUtil(RestTemplate restTemplate) {
        return () -> RestUtils.setRestTemplate(restTemplate);
    }

    @Configuration
    @ConditionalOnClass(Aspect.class)
    public static class LodsveWebDebugConfiguration {
        @Bean
        @Order(1)
        @ConditionalOnProperty(name = "lodsve.web-mvc.is-debug")
        public DebugRequestAspect debugRequestAspect(ObjectProvider<ObjectMapper> objectMapper, ObjectProvider<WebMvcProperties> objectProvider) {
            WebMvcProperties properties = Objects.requireNonNull(objectProvider.getIfAvailable());
            return new DebugRequestAspect(objectMapper.getIfAvailable(), properties.getDebug().getExcludeUrl(), properties.getDebug().getExcludeAddress());
        }
    }

    @Configuration
    @ConditionalOnClass(Docket.class)
    public static class SwaggerIgnoreWebInputWebOutConfiguration {
        @Bean
        public AlternateTypeRuleConvention ignoreWebInputWebOutConvention(ObjectProvider<TypeResolver> objectProvider) {
            TypeResolver resolver = (null == objectProvider.getIfUnique() ? new TypeResolver() : objectProvider.getIfUnique());
            return new AlternateTypeRuleConvention() {
                @Override
                public int getOrder() {
                    return Ordered.HIGHEST_PRECEDENCE;
                }

                @Override
                public List<AlternateTypeRule> rules() {
                    return Lists.newArrayList(newRule(resolver.resolve(WebOutput.class), resolver.resolve(Void.class)),
                        newRule(resolver.resolve(WebInput.class), resolver.resolve(Void.class)));
                }
            };
        }
    }
}
