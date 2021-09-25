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
package com.lodsve.boot.autoconfigure.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.lodsve.boot.bean.Codeable;
import com.lodsve.boot.json.GsonConverter;
import com.lodsve.boot.json.JacksonConverter;
import com.lodsve.boot.json.JsonConverter;
import com.lodsve.boot.json.gson.CodeableTypeAdapterFactory;
import com.lodsve.boot.json.jackson.CodeableEnumDeserializer;
import com.lodsve.boot.json.jackson.CodeableEnumSerializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

/**
 * json配置.
 *
 * @author Hulk Sun
 */
@Configuration
public class JsonAutoConfiguration {
    @Configuration
    @ConditionalOnClass(ObjectMapper.class)
    @AutoConfigureAfter(JacksonAutoConfiguration.class)
    public static class JacksonSerializerConfiguration {

        @Bean
        public Jackson2ObjectMapperBuilderCustomizer lodsveCustomizer(List<com.fasterxml.jackson.databind.Module> modules) {
            return builder -> {
                //日期的处理
                //默认是将日期类型转换为yyyy-MM-dd HH:mm
                //如果需要自定义的，则在pojo对象的Date类型上加上注解
                //@com.fasterxml.jackson.annotation.JsonFormat(pattern = "时间格式化")
                builder.timeZone(TimeZone.getDefault());
                builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

                builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

                // modules
                builder.modules(modules);
            };
        }

        @Bean
        public com.fasterxml.jackson.databind.Module defaultCustomizer() {
            SimpleModule timeModule = new SimpleModule("timeModule");
            timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

            timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

            return timeModule;
        }

        @Bean
        public com.fasterxml.jackson.databind.Module codeableCustomizer() {
            // 序列化枚举时的处理
            SimpleModule codeableModule = new SimpleModule("codeableModule");
            codeableModule.addSerializer(Enum.class, new CodeableEnumSerializer());
            codeableModule.addDeserializer(Enum.class, new CodeableEnumDeserializer());

            return codeableModule;
        }

        @Bean
        public JsonConverter jsonConverter(ObjectProvider<ObjectMapper> objectProvider) {
            return new JacksonConverter(objectProvider.getIfAvailable());
        }
    }

    @Configuration
    @ConditionalOnClass(Gson.class)
    @AutoConfigureAfter(GsonAutoConfiguration.class)
    public static class GsonSerializerConfiguration implements BeanFactoryAware {
        private static final Logger logger = LoggerFactory.getLogger(GsonSerializerConfiguration.class);
        private BeanFactory beanFactory;

        @Bean
        public GsonBuilderCustomizer codeableGsonBuilderCustomizer() {
            return builder -> {
                Set<Class<Codeable>> enums = scan();
                enums.forEach(c -> {
                    builder.registerTypeAdapterFactory(new CodeableTypeAdapterFactory(c));
                });
            };
        }

        @Bean
        public GsonBuilderCustomizer localDateTimeGsonBuilderCustomizer() {
            return builder -> builder.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                String datetime = json.getAsJsonPrimitive().getAsString();
                return LocalDateTime.parse(datetime.substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }).registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> {
                return new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            });
        }

        @SuppressWarnings("unchecked")
        private Set<Class<Codeable>> scan() {
            List<String> packages = AutoConfigurationPackages.get(beanFactory);

            BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
            ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);
            TypeFilter tf = new AssignableTypeFilter(Codeable.class);
            s.setIncludeAnnotationConfig(false);
            s.addIncludeFilter(tf);
            s.scan(packages.toArray(new String[0]));

            String[] beans = bdr.getBeanDefinitionNames();
            Set<Class<Codeable>> classes = Sets.newHashSet();

            for (String b : beans) {
                BeanDefinition bd = bdr.getBeanDefinition(b);
                if (StringUtils.isBlank(bd.getBeanClassName())) {
                    continue;
                }

                try {
                    classes.add((Class<Codeable>) ClassUtils.forName(bd.getBeanClassName(), Thread.currentThread().getContextClassLoader()));
                } catch (ClassNotFoundException ignored) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("class name is '{}' can not be found!", bd.getBeanClassName());
                    }
                }
            }
            return classes;

        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Bean
        public JsonConverter jsonConverter(ObjectProvider<Gson> gson) {
            return new GsonConverter(gson.getIfAvailable());
        }
    }
}
