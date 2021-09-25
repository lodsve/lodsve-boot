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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 分页参数.
 *
 * @author Hulk Sun
 */
public class PageableWebMvcConfigurer implements WebMvcConfigurer {
    private final List<PageableHandlerMethodArgumentResolverCustomizer> pageableResolverCustomizers;
    private final List<SortHandlerMethodArgumentResolverCustomizer> sortResolverCustomizers;

    PageableWebMvcConfigurer(List<PageableHandlerMethodArgumentResolverCustomizer> pageableResolverCustomizers, List<SortHandlerMethodArgumentResolverCustomizer> sortResolverCustomizers) {
        this.pageableResolverCustomizers = pageableResolverCustomizers;
        this.sortResolverCustomizers = sortResolverCustomizers;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver(sortResolver());
        if (CollectionUtils.isNotEmpty(sortResolverCustomizers)) {
            pageableResolverCustomizers.forEach(p -> p.customize(pageableResolver));
        }

        resolvers.add(pageableResolver);
    }

    private SortHandlerMethodArgumentResolver sortResolver() {
        SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
        if (CollectionUtils.isNotEmpty(sortResolverCustomizers)) {
            sortResolverCustomizers.forEach(s -> s.customize(sortResolver));
        }
        return sortResolver;
    }
}
