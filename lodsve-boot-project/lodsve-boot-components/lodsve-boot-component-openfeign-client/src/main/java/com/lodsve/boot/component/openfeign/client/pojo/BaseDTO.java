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
package com.lodsve.boot.component.openfeign.client.pojo;

import com.google.gson.Gson;
import org.springframework.util.ClassUtils;

import java.io.Serializable;

/**
 * 所有与RPC相关的出入参皆继承于此类.
 *
 * @author Hulk Sun
 */
public class BaseDTO implements Serializable {
    private static final boolean GSON_EXIST;
    private static final String GSON_PKG = "com.google.gson.Gson";

    static {
        GSON_EXIST = ClassUtils.isPresent(GSON_PKG, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public String toString() {
        if (GSON_EXIST) {
            return new Gson().toJson(this);
        }

        return super.toString();
    }
}
