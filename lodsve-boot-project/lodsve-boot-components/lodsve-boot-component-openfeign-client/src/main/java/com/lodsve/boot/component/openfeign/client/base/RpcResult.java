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
package com.lodsve.boot.component.openfeign.client.base;

import com.lodsve.boot.component.openfeign.client.pojo.BaseDTO;

/**
 * 所有RPC返回对象都是RpcResult.
 *
 * @author Hulk Sun
 */
public class RpcResult<T> extends BaseDTO {
    public static final String CODE_SUCCESS = "0";
    private String code;
    private T data;
    private String message;
    private String error;

    public RpcResult() {
    }

    public RpcResult(T data) {
        this.code = "0";
        this.data = data;
    }

    public RpcResult(T data, String message) {
        this.code = "0";
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

