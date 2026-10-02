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

import com.lodsve.boot.component.validator.annotations.NotNull;
import com.lodsve.boot.component.validator.annotations.ValidateEntity;
import com.lodsve.boot.component.validator.exception.ErrorMessage;
import com.lodsve.boot.component.validator.exception.ExceptionHandler;
import com.lodsve.boot.component.validator.exception.VerifyFailedException;
import com.lodsve.boot.component.validator.handler.AbstractValidateHandler;
import com.lodsve.boot.utils.I18nMessageUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ValidatorEngineTest {
    private final CapturingExceptionHandler errors = new CapturingExceptionHandler();
    private final MinimumLengthHandler handler = new MinimumLengthHandler();
    private final ValidatorEngine engine = new ValidatorEngine(errors, Collections.singletonList(handler));

    @Before
    public void configureMessages() {
        StaticMessageSource messages = new StaticMessageSource();
        messages.setUseCodeAsDefaultMessage(true);
        new I18nMessageUtil().setMessageSource(messages);
    }

    @Test
    public void dispatchesTypedAnnotationsAndProceedsWhenValid() throws Throwable {
        AtomicBoolean proceeded = new AtomicBoolean();
        Assert.assertEquals("done", engine.validate(invocation(new Entity("valid"), proceeded)));
        Assert.assertTrue(proceeded.get());
        Assert.assertEquals(1, handler.calls);
        Assert.assertEquals(3, handler.minimum);
        Assert.assertEquals("valid", handler.value);
    }

    @Test
    public void reportsBuiltinValidationFailure() {
        AtomicBoolean proceeded = new AtomicBoolean();
        Assert.assertThrows(VerifyFailedException.class, () -> engine.validate(invocation(new Entity(null), proceeded)));
        Assert.assertFalse(proceeded.get());
        Assert.assertEquals(1, errors.messages.size());
        Assert.assertEquals(NotNull.class, errors.messages.get(0).getAnnotation());
        Assert.assertNull(errors.messages.get(0).getValue());
    }

    @Test
    public void preservesCustomValidationErrorDetails() {
        AtomicBoolean proceeded = new AtomicBoolean();
        Assert.assertThrows(VerifyFailedException.class, () -> engine.validate(invocation(new Entity("x"), proceeded)));
        Assert.assertFalse(proceeded.get());
        Assert.assertEquals(1, errors.messages.size());
        ErrorMessage error = errors.messages.get(0);
        Assert.assertEquals(MinimumLength.class, error.getAnnotation());
        Assert.assertEquals(Entity.class, error.getClazz());
        Assert.assertEquals("name", error.getField().getName());
        Assert.assertEquals("x", error.getValue());
    }

    private ProceedingJoinPoint invocation(Entity entity, AtomicBoolean proceeded) {
        return (ProceedingJoinPoint) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ProceedingJoinPoint.class}, (proxy, method, args) -> {
            if ("getArgs".equals(method.getName())) {
                return new Object[]{entity};
            }
            if ("proceed".equals(method.getName())) {
                proceeded.set(true);
                return "done";
            }
            return null;
        });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface MinimumLength {
        int value();
    }

    @ValidateEntity
    public static class Entity {
        @NotNull
        @MinimumLength(3)
        private final String name;

        Entity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class MinimumLengthHandler extends AbstractValidateHandler<MinimumLength> {
        private int calls;
        private int minimum;
        private Object value;

        @Override
        protected ErrorMessage handle(MinimumLength annotation, Object value) {
            calls++;
            minimum = annotation.value();
            this.value = value;
            if (value != null && value.toString().length() < minimum) {
                return new ErrorMessage(MinimumLength.class, MinimumLengthHandler.class, "too short");
            }
            return null;
        }
    }

    private static class CapturingExceptionHandler extends ExceptionHandler {
        private List<ErrorMessage> messages;

        @Override
        public String getMessage(List<ErrorMessage> messages) {
            this.messages = messages;
            return "validation failed";
        }
    }
}
