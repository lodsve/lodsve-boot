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

import com.lodsve.boot.component.validator.exception.ErrorMessage;

import java.lang.annotation.Annotation;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public interface ValidateHandler<T extends Annotation> {
    /**
     * 在抽象类中进行每个验证组件都会执行的操作(判空)
     *
     * @param annotation 待验证字段的注解
     * @param value      待验证字段的值
     * @return 异常信息
     */
    ErrorMessage validate(T annotation, Object value);
}
