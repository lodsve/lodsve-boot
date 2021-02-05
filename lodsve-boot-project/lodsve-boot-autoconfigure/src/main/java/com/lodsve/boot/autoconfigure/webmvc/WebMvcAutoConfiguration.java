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
package com.lodsve.boot.autoconfigure.webmvc;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.lodsve.boot.json.JsonConverter;
import com.lodsve.boot.webmvc.debug.DebugRequestAspect;
import com.lodsve.boot.webmvc.resolver.WebInput;
import com.lodsve.boot.webmvc.resolver.WebOutput;
import com.lodsve.boot.webmvc.response.LodsveBootExceptionHandler;
import com.lodsve.boot.webmvc.response.WebResultResponseWrapperHandler;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ConditionalOnWebApplication
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
