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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import com.lodsve.boot.webmvc.debug.DebugRequestAspect;
import com.lodsve.boot.webmvc.json.CodeableEnumDeserializer;
import com.lodsve.boot.webmvc.json.CodeableEnumSerializer;
import com.lodsve.boot.webmvc.resolver.WebInput;
import com.lodsve.boot.webmvc.resolver.WebOutput;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.spring.web.plugins.Docket;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * web mvc 配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ConditionalOnWebApplication
@ConditionalOnClass(CodeableEnumSerializer.class)
@EnableConfigurationProperties(WebMvcProperties.class)
@Configuration
public class WebMvcAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setTimeZone(TimeZone.getDefault());

        // 序列化枚举时的处理
        SimpleModule serializerModule = new SimpleModule("codeableEnumSerializer");
        serializerModule.addSerializer(Enum.class, new CodeableEnumSerializer());
        objectMapper.registerModule(serializerModule);

        // 反序列化枚举时的处理
        SimpleModule deserializerModule = new SimpleModule("codeableEnumDeserializer");
        deserializerModule.addDeserializer(Enum.class, new CodeableEnumDeserializer());
        objectMapper.registerModule(deserializerModule);

        //日期的处理
        //默认是将日期类型转换为yyyy-MM-dd HH:mm
        //如果需要自定义的，则在pojo对象的Date类型上加上注解
        //@com.fasterxml.jackson.annotation.JsonFormat(pattern = "时间格式化")
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : messageConverters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                messageConverters.remove(converter);

                MappingJackson2HttpMessageConverter newConverter = new MappingJackson2HttpMessageConverter();
                newConverter.setObjectMapper(objectMapper);
                messageConverters.add(newConverter);
            }
        }
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Configuration
    @ConditionalOnClass(Aspect.class)
    public static class LodsveWebDebugConfiguration {
        @Bean
        @Order(1)
        @ConditionalOnProperty(name = "lodsve.web-mvc.is-debug")
        public DebugRequestAspect debugRequestAspect(ObjectProvider<ObjectMapper> objectMapper, WebMvcProperties properties) {
            return new DebugRequestAspect(objectMapper.getIfAvailable(), properties.getDebug().getExcludeUrl(), properties.getDebug().getExcludeAddress());
        }
    }

    @Configuration
    public static class LodsveWebMvcConfiguration {
        @Bean
        public WebMvcConfigurer webMvcConfigurer(ObjectProvider<ObjectMapper> objectMapper) {
            return new LodsveWebMvcConfigurer(objectMapper.getIfAvailable());
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
