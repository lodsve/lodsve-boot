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
package com.lodsve.boot.actuator.enums;

import com.lodsve.boot.actuator.enums.EnumDescription.EnumDetail;
import com.lodsve.boot.bean.Codeable;
import com.lodsve.boot.bean.Description;
import com.lodsve.boot.utils.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * enums endpoint.
 *
 * @author Hulk Sun
 */
@WebEndpoint(id = "enums")
public class EnumsEndpoint {
    private static final List<EnumDescription> ENUM_DESCRIPTIONS = new ArrayList<>(10);
    private final String[] basePackages;

    public EnumsEndpoint(String[] basePackages) {
        this.basePackages = basePackages;
    }

    @ReadOperation
    public List<EnumDescription> listEnums() throws ClassNotFoundException {
        if (!ENUM_DESCRIPTIONS.isEmpty()) {
            return ENUM_DESCRIPTIONS;
        }
        BeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

        TypeFilter filter = new AssignableTypeFilter(Codeable.class);
        scanner.setIncludeAnnotationConfig(false);
        scanner.resetFilters(false);
        scanner.addIncludeFilter(filter);
        scanner.scan(basePackages);

        String[] beans = registry.getBeanDefinitionNames();
        for (String bean : beans) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(bean);
            if (null == beanDefinition.getBeanClassName()) {
                continue;
            }

            Class<?> clazz = ClassUtils.forName(beanDefinition.getBeanClassName(), Thread.currentThread().getContextClassLoader());
            String name = clazz.getName();
            String description = beanDefinition.getBeanClassName();
            if (clazz.isAnnotationPresent(Description.class)) {
                description = clazz.getAnnotation(Description.class).value();
            }

            ENUM_DESCRIPTIONS.add(new EnumDescription(clazz.getSimpleName(), name, description, getDetails(clazz)));
        }

        return ENUM_DESCRIPTIONS;
    }

    @ReadOperation
    public EnumDescription showDetail(@Selector String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name should be not null or not empty!");
        }

        EnumDescription enumDescription = ListUtils.findOne(ENUM_DESCRIPTIONS, item -> item.getName().equals(name));
        if (null == enumDescription) {
            enumDescription = ListUtils.findOne(ENUM_DESCRIPTIONS, item -> item.getShortName().equals(name));
        }

        if (null == enumDescription) {
            throw new IllegalArgumentException("enum is not exist!");
        }

        return enumDescription;
    }

    private List<EnumDetail> getDetails(Class<?> enumClass) {
        if (null == enumClass || !enumClass.isEnum() || enumClass.isAssignableFrom(Codeable.class)) {
            return Collections.emptyList();
        }

        Codeable[] enumConstants = (Codeable[]) enumClass.getEnumConstants();

        return Arrays.stream(enumConstants).map(constant -> new EnumDetail(constant.getCode(), constant.getTitle())).collect(Collectors.toList());
    }
}
