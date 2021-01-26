/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
 * @author 孙昊(Hulk)
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
