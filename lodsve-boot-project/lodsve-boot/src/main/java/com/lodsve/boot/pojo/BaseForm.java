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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 数据查询对象，各层接收上层的查询请求。注意超过2 个参数的查询封装，禁止
 * 使用Map 类来传输
 *
 * @author Hulk Sun
 */
@Setter
@Getter
public class BaseForm implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 表单提交的主键,用来
     */
    private Long id;

    /**
     * 用来区分是修改还是新增
     */
    @JsonIgnore
    public Boolean isCreate() {
        return null == id;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
