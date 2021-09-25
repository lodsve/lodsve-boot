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
package com.lodsve.boot.component.validator.handler;

import com.lodsve.boot.component.validator.constant.ValidateConstants;
import com.lodsve.boot.component.validator.core.ValidateHandler;
import com.lodsve.boot.component.validator.exception.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * 所有验证条件的处理类必须继承这个抽象类,以实现各自的验证方法.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public abstract class AbstractValidateHandler<T extends Annotation> implements ValidateHandler<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractValidateHandler.class);

    /**
     * 所有继承这个类的类才会是对应注解验证类型的验证类,验证的时候都调用这个方法
     *
     * @param annotation 待验证字段的注解
     * @param value      待验证字段的值
     * @return 异常信息
     */
    protected abstract ErrorMessage handle(T annotation, Object value);

    @Override
    public ErrorMessage validate(T annotation, Object value) {
        if (annotation == null) {
            logger.error("given null annotation!");
            return null;
        }

        return this.handle(annotation, value);
    }

    protected ErrorMessage getMessage(Class<T> annotation, Class<? extends AbstractValidateHandler<T>> handler, String key, boolean result, Object... args) {
        if (result) {
            return null;
        }

        return new ErrorMessage(annotation, handler, ValidateConstants.getMessage(key, args));
    }
}
