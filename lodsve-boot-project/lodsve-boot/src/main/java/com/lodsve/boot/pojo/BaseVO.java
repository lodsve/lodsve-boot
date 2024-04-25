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
package com.lodsve.boot.pojo;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * 显示层对象，通常是 Web 向模板渲染引擎层传输的对象
 *
 * @author Hulk Sun
 */
public class BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
