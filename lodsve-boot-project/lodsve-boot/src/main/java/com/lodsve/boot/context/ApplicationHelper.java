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
package com.lodsve.boot.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * applicationContext的辅助类
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2012-3-8 下午09:56:58
 */
public class ApplicationHelper {
    private static final ApplicationHelper INSTANCE = new ApplicationHelper();
    private static final Set<ApplicationContext> APPLICATIONS = new HashSet<>();

    /**
     * 构造器私有，不可在外部进行初始化实例
     */
    private ApplicationHelper() {

    }

    /**
     * 根据class获取bean
     *
     * @param clazz type the bean must match; can be an interface or superclass. null is disallowed.
     * @param <T>   class
     * @return an instance of the single bean matching the required type
     */
    public <T> T getBean(Class<T> clazz) {
        for (ApplicationContext app : APPLICATIONS) {
            try {
                return app.getBean(clazz);
            } catch (BeansException ignored) {
            }
        }

        throw new NoSuchBeanDefinitionException(clazz.getName());
    }

    /**
     * 根据bean的名称获取bean
     *
     * @param name the name of the bean to retrieve
     * @return an instance of the bean
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        for (ApplicationContext app : APPLICATIONS) {
            try {
                return (T) app.getBean(name);
            } catch (BeansException ignored) {
            }
        }

        throw new NoSuchBeanDefinitionException(name);
    }

    /**
     * 获取spring上下文中的所有指定类型的bean
     *
     * @param clazz the class or interface to match, or null for all concrete beans
     * @return a Map with the matching beans, containing the bean names as keys and the corresponding bean instances as values
     */
    public <T> Map<String, T> getBeansByType(Class<T> clazz) {
        Map<String, T> results = new HashMap<>(16);

        APPLICATIONS.forEach(app -> results.putAll(app.getBeansOfType(clazz)));
        return results;
    }

    public void addApplicationContext(ApplicationContext context) {
        if (context == null) {
            return;
        }
        APPLICATIONS.add(context);

        if (context.getParent() != null) {
            //递归，将context的所有上一级放入apps中
            this.addApplicationContext(context.getParent());
        }
    }

    public static ApplicationHelper getInstance() {
        return INSTANCE;
    }

}
