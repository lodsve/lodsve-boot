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
package com.lodsve.boot.component.webmvc.response;

import com.lodsve.boot.bean.WebResult;
import com.lodsve.boot.exception.LodsveBootException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统一异常处理,这些异常说明是业务无法处理，区分statusCode供前端使用不同方式处理。
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ControllerAdvice(annotations = RestController.class)
public class LodsveBootExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(LodsveBootExceptionHandler.class);

    /**
     * 捕获自定义异常
     *
     * @param exception exception
     * @return 异常处理
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LodsveBootException.class)
    @ResponseBody
    public WebResult<Object> handleException(final LodsveBootException exception) {
        if (logger.isErrorEnabled()) {
            logger.error(exception.getMessage(), exception);
        }
        int code = (null == exception.getCode()) ? HttpStatus.BAD_REQUEST.value() : exception.getCode();
        String message = StringUtils.isNotBlank(exception.getMessage()) ? exception.getMessage() : HttpStatus.BAD_REQUEST.getReasonPhrase();
        return WebResult.error(null, code, message);
    }

    /**
     * 未被捕获的异常处理
     *
     * @param throwable exception
     * @return 异常处理
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public WebResult<Object> handleException(final Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(throwable.getMessage(), throwable);
        }

        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = StringUtils.isNotBlank(throwable.getMessage()) ? throwable.getMessage() : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        return WebResult.error(null, code, message);

    }
}
