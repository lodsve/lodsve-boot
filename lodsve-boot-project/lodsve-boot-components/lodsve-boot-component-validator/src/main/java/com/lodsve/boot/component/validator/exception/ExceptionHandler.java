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
package com.lodsve.boot.component.validator.exception;

import com.lodsve.boot.component.validator.constant.ValidateConstants;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 异常处理类,如果要实现自定义异常,则全部继承这个类.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public abstract class ExceptionHandler {

    /**
     * 处理异常的方法
     *
     * @param messages 检验结果
     * @throws VerifyFailedException 校验失败的异常
     */
    public final void doHandleException(List<ErrorMessage> messages) throws VerifyFailedException {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }

        throw new VerifyFailedException(ValidateConstants.getMessage("error-occurred") + "\r\n" + getMessage(messages));
    }

    /**
     * 获取异常信息中的message
     *
     * @param messages 异常信息
     * @return 异常信息中的message
     */
    public abstract String getMessage(List<ErrorMessage> messages);
}
