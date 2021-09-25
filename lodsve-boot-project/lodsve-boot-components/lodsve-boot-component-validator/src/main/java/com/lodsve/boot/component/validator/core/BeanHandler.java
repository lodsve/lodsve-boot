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
package com.lodsve.boot.component.validator.core;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;

/**
 * 类似于map,放置验证注解和其对应的处理类.
 *
 * @author Hulk Sun
 */
public class BeanHandler {
    /**
     * 唯一标识
     */
    private String key;
    /**
     * 注解
     */
    private Class<? extends Annotation> annotation;
    /**
     * 其对应的处理类
     */
    private ValidateHandler<? extends Annotation> validateHandler;

    public BeanHandler(String key, Class<? extends Annotation> annotation, ValidateHandler<? extends Annotation> validateHandler) {
        if (StringUtils.isEmpty(key)) {
            this.key = StringUtils.substring(annotation.getSimpleName(), 0, annotation.getSimpleName().lastIndexOf("_"));
        } else {
            this.key = key;
        }
        this.annotation = annotation;
        this.validateHandler = validateHandler;
    }

    public BeanHandler(Class<? extends Annotation> annotation, ValidateHandler<? extends Annotation> validateHandler) {
        this.key = StringUtils.substring(annotation.getSimpleName(), 0, annotation.getSimpleName().lastIndexOf("_"));
        this.annotation = annotation;
        this.validateHandler = validateHandler;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public ValidateHandler<? extends Annotation> getValidateHandler() {
        return validateHandler;
    }

    public void setValidateHandler(ValidateHandler<? extends Annotation> validateHandler) {
        this.validateHandler = validateHandler;
    }
}
