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

import com.google.common.collect.Sets;
import com.lodsve.boot.utils.I18nMessageUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Set;

/**
 * core包的配置.
 *
 * @author sunhao(hulk)
 */
@Configuration
@EnableConfigurationProperties({CoreProperties.class})
@AutoConfigureAfter(HttpMessageConvertersAutoConfiguration.class)
public class CoreAutoConfiguration {
    @Bean
    public MessageSource messageSource(CoreProperties coreProperties) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        Set<String> i18nFolder = coreProperties.getI18nFolders();
        if (CollectionUtils.isEmpty(i18nFolder)) {
            i18nFolder = Sets.newHashSet();
        }
        i18nFolder.add("i18n.exceptionMessage");
        i18nFolder.add("i18n.validatorMessage");
        i18nFolder.add("i18n.commonMessage");
        i18nFolder.add("i18n.i18nMessage");

        messageSource.setBasenames(i18nFolder.toArray(new String[0]));
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public I18nMessageUtil messageUtil() {
        return new I18nMessageUtil();
    }

    @Configuration
    @ConditionalOnClass({Pageable.class, PageableHandlerMethodArgumentResolverCustomizer.class})
    public static class PageableConfiguration {
        private final List<PageableHandlerMethodArgumentResolverCustomizer> pageableResolverCustomizers;
        private final List<SortHandlerMethodArgumentResolverCustomizer> sortResolverCustomizers;

        public PageableConfiguration(ObjectProvider<List<PageableHandlerMethodArgumentResolverCustomizer>> pageableResolverCustomizers, ObjectProvider<List<SortHandlerMethodArgumentResolverCustomizer>> sortResolverCustomizers) {
            this.pageableResolverCustomizers = pageableResolverCustomizers.getIfAvailable();
            this.sortResolverCustomizers = sortResolverCustomizers.getIfAvailable();
        }

        @Bean
        public WebMvcConfigurer pageableWebConfiguration() {
            return new PageableWebMvcConfigurer(pageableResolverCustomizers, sortResolverCustomizers);
        }
    }
}
