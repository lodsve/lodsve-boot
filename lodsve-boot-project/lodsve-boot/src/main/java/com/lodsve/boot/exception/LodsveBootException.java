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
package com.lodsve.boot.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * 系统中其他所有的异常均需要继承.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 15/8/14 下午12:27
 */
public class LodsveBootException extends NestedRuntimeException {
    private static final Integer BAD_REQUEST_CODE = 400;
    /**
     * 异常code
     */
    private final Integer code;

    public LodsveBootException(String message) {
        super(message);
        this.code = BAD_REQUEST_CODE;
    }

    /**
     * @param code    异常编码，在i18n配置文件中配置的编码，请确保该异常编码已经定义
     * @param message 后台异常内容，这个内容主要用于输出后台日志，便于异常诊断
     */
    public LodsveBootException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
