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
package com.lodsve.boot.example.enums;

import com.lodsve.boot.bean.Codeable;

/**
 * 用户状态枚举.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 15/8/10 上午11:45
 */
public enum Status implements Codeable {
    ENABLED("100", "启用"), DISABLED("101", "禁用"), AUDITING("102", "审核中"), DELETED("103", "已删除");

    private String code;
    private String title;

    Status(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
