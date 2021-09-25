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

import com.lodsve.boot.component.validator.annotations.ValidateEntity;
import com.lodsve.boot.component.validator.exception.DefaultExceptionHandler;
import com.lodsve.boot.component.validator.exception.ErrorMessage;
import com.lodsve.boot.component.validator.exception.ExceptionHandler;
import com.lodsve.boot.component.validator.handler.ChineseHandler;
import com.lodsve.boot.component.validator.handler.DoubleHandler;
import com.lodsve.boot.component.validator.handler.EmailHandler;
import com.lodsve.boot.component.validator.handler.EnglishHandler;
import com.lodsve.boot.component.validator.handler.IdCardHandler;
import com.lodsve.boot.component.validator.handler.IntegerHandler;
import com.lodsve.boot.component.validator.handler.IpHandler;
import com.lodsve.boot.component.validator.handler.LimitHandler;
import com.lodsve.boot.component.validator.handler.MobileHandler;
import com.lodsve.boot.component.validator.handler.NotNullHandler;
import com.lodsve.boot.component.validator.handler.NumberHandler;
import com.lodsve.boot.component.validator.handler.PasswordHandler;
import com.lodsve.boot.component.validator.handler.QqHandler;
import com.lodsve.boot.component.validator.handler.RegexHandler;
import com.lodsve.boot.component.validator.handler.TelephoneHandler;
import com.lodsve.boot.component.validator.handler.UrlHandler;
import com.lodsve.boot.component.validator.handler.ZipHandler;
import com.lodsve.boot.utils.GenericUtils;
import com.lodsve.boot.utils.ObjectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 验证引擎核心组件.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Aspect
public class ValidatorEngine {
    private static final Logger logger = LoggerFactory.getLogger(ValidatorEngine.class);

    /**
     * 异常处理类,默认使用DefaultExceptionHandler
     */
    private final ExceptionHandler exceptionHandler;
    private final List<ValidateHandler<? extends Annotation>> handlers;
    /**
     * key-value(注解名称-beanHandler)
     */
    private final Map<String, BeanHandler> beanHandlerMap;
    /**
     * 注解的集合(集合的名称)
     */
    private final List<Class<? extends Annotation>> annotationClasses;
    /**
     * 注解的集合(集合的名称)
     */
    private final List<String> annotations;

    /**
     * 需要验证的类--需要验证的字段(内存中的缓存)
     */
    private final Map<String, List<Field>> validateFields;

    public ValidatorEngine(ExceptionHandler exceptionHandler, List<ValidateHandler<?>> handlers) {
        this.exceptionHandler = (exceptionHandler == null ? new DefaultExceptionHandler() : exceptionHandler);
        this.handlers = new ArrayList<>(16);
        beanHandlerMap = new HashMap<>(16);
        validateFields = new HashMap<>(16);
        annotations = new ArrayList<>(16);
        annotationClasses = new ArrayList<>(16);

        if (CollectionUtils.isNotEmpty(handlers)) {
            this.handlers.addAll(handlers);
        }

        initValidateEngine();
    }

    /**
     * 初始化验证引擎
     */
    private void initValidateEngine() {
        handlers.add(new ChineseHandler());
        handlers.add(new DoubleHandler());
        handlers.add(new EmailHandler());
        handlers.add(new EnglishHandler());
        handlers.add(new IdCardHandler());
        handlers.add(new IntegerHandler());
        handlers.add(new IpHandler());
        handlers.add(new LimitHandler());
        handlers.add(new MobileHandler());
        handlers.add(new NotNullHandler());
        handlers.add(new NumberHandler());
        handlers.add(new PasswordHandler());
        handlers.add(new QqHandler());
        handlers.add(new RegexHandler());
        handlers.add(new TelephoneHandler());
        handlers.add(new UrlHandler());
        handlers.add(new ZipHandler());

        resolveAnnotation();
    }

    @SuppressWarnings("unchecked")
    private void resolveAnnotation() {
        handlers.forEach(h -> {
            Class<?> genericType = GenericUtils.getGenericParameter0(h.getClass());
            if (!Annotation.class.isAssignableFrom(genericType)) {
                return;
            }
            BeanHandler beanHandler = new BeanHandler(genericType.getSimpleName(), (Class<? extends Annotation>) genericType, h);

            // 将beanHandlers中的注解-处理类放入beanHandlerMap中(并将注解中文名放入内存中-annotations)
            beanHandlerMap.put(genericType.getSimpleName(), beanHandler);
            // 将所有的注解放入内存中
            annotations.add(genericType.getSimpleName());
            annotationClasses.add((Class<? extends Annotation>) genericType);
        });
    }

    /**
     * 基于spring AOP的"环绕"验证开始
     *
     * @param proceedingJoinPoint 切面
     * @return 返回值
     * @throws Throwable 异常
     */
    @Around("@annotation(com.lodsve.boot.component.validator.core.NeedValidate)")
    public Object validate(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.debug("start validate..., proceedingJoinPoint is '{}'!", proceedingJoinPoint);

        //获取所有参数
        Object[] args = proceedingJoinPoint.getArgs();
        logger.debug("get args is '{}'!", args);

        List<ErrorMessage> errorMessages = new ArrayList<>();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }

            Class<?> clazz = arg.getClass();
            if (clazz.isAnnotationPresent(ValidateEntity.class)) {
                errorMessages.addAll(validateEntityFields(arg));
            }
        }

        if (CollectionUtils.isNotEmpty(errorMessages)) {
            // 处理异常
            this.exceptionHandler.doHandleException(errorMessages);
        }

        return proceedingJoinPoint.proceed();
    }

    /**
     * 对entity每个字段进行验证
     *
     * @param entity 要验证的entity
     */
    private List<ErrorMessage> validateEntityFields(Object entity) {
        if (entity == null) {
            logger.error("given empty entity!");
            return Collections.emptyList();
        }
        List<Field> fieldList = getValidateFields(entity.getClass());
        if (CollectionUtils.isEmpty(fieldList)) {
            logger.debug("given entity class is '{}' has no validate fields!", entity.getClass());
            return Collections.emptyList();
        }

        List<ErrorMessage> errorMessages = new ArrayList<>();
        for (Field f : fieldList) {
            errorMessages.addAll(validateField(f, entity));
        }

        return errorMessages;
    }

    /**
     * 验证字段
     *
     * @param f      待验证字段
     * @param entity 待验证实体
     */
    @SuppressWarnings("all")
    private List<ErrorMessage> validateField(final Field f, final Object entity) {
        if (f == null || entity == null) {
            logger.error("given field is null or entity is null!");
            return Collections.emptyList();
        }
        Annotation[] as = f.getAnnotations();

        List<ErrorMessage> messages = new ArrayList<>();
        for (Annotation a : as) {
            if (!annotationClasses.contains(a.annotationType())) {
                continue;
            }

            BeanHandler bh = this.beanHandlerMap.get(a.annotationType().getSimpleName());
            if (bh == null) {
                continue;
            }

            ValidateHandler handler = bh.getValidateHandler();
            if (handler == null) {
                logger.error("handler is null for annotation '{}'", a);
                continue;
            }

            Object value = ObjectUtils.getFieldValue(entity, f.getName());
            ErrorMessage message = handler.validate(a, value);
            if (message != null) {
                message.setClazz(entity.getClass());
                message.setField(f);
                message.setValue(value);

                messages.add(message);
            }
        }

        return messages;
    }

    /**
     * 获取指定类需要验证的字段,如果内存中没有做缓存,则循环取出来,否则取内存中的
     *
     * @param clazz 指定类
     * @return 需要验证的字段
     */
    private List<Field> getValidateFields(Class<?> clazz) {
        if (clazz == null) {
            logger.error("given empty class!");
            return Collections.emptyList();
        }

        String key = "validate-" + clazz.getName();
        List<Field> fieldList = this.validateFields.get(key);
        // 内存中不存在这个类的需要验证字段
        if (CollectionUtils.isEmpty(fieldList)) {
            fieldList = new ArrayList<>();
            Field[] fields = ObjectUtils.getFields(clazz);
            for (Field f : fields) {
                Annotation[] ans = f.getAnnotations();
                for (Annotation a : ans) {
                    if (this.annotations.contains(a.annotationType().getSimpleName())) {
                        fieldList.add(f);
                        //这个字段只要有一个注解是符合条件的,立马放入待验证字段中
                        break;
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(fieldList)) {
                //放入内存中
                this.validateFields.put(key, fieldList);
            }
        }

        return fieldList;
    }
}
