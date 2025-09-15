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
package com.lodsve.boot.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 返回结果集.
 *
 * @author Hulk Sun
 */
public class WebResult<T> implements Serializable {
    private static final Integer SUCCESS = 200;
    private static final Integer ERROR = 500;
    private static final String OK_MESSAGE = "Successful";
    private static final String ERROR_MESSAGE = "Some internal exceptions occurred";

    private Boolean result;
    private Integer code;
    private T data;
    private String message;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static <T> WebResult<T> ok(T data) {
        WebResult<T> result = new WebResult<>();
        result.setCode(SUCCESS);
        result.setResult(true);
        result.setData(data);
        result.setMessage(OK_MESSAGE);

        return result;
    }

    public static <T> WebResult<T> ok(T data, String message) {
        WebResult<T> result = new WebResult<>();
        result.setCode(SUCCESS);
        result.setResult(true);
        result.setData(data);
        result.setMessage(StringUtils.isBlank(message) ? OK_MESSAGE : message);

        return result;
    }

    public static <T> WebResult<T> error() {
        WebResult<T> result = new WebResult<>();
        result.setCode(ERROR);
        result.setResult(false);
        result.setData(null);
        result.setMessage(ERROR_MESSAGE);

        return result;
    }

    public static <T> WebResult<T> error(T data) {
        WebResult<T> result = new WebResult<>();
        result.setCode(ERROR);
        result.setResult(false);
        result.setData(data);
        result.setMessage(ERROR_MESSAGE);

        return result;
    }

    public static <T> WebResult<T> error(T data, Integer code, String message) {
        WebResult<T> result = new WebResult<>();
        result.setCode(null == code ? ERROR : code);
        result.setResult(false);
        result.setData(data);
        result.setMessage(StringUtils.isBlank(message) ? ERROR_MESSAGE : message);

        return result;
    }

    public static <T> WebResult<T> of(boolean result) {
        return result ? WebResult.ok(null) : WebResult.error();
    }

    @Override
    public String toString() {
        return "WebResult [result=" + result + ", code=" + code + ", message=" + message + ", data=" + data + "]";
    }
}
