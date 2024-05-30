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
package com.lodsve.boot.example.countdown;

import com.lodsve.boot.component.countdown.CountdownEventType;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/12 下午9:38
 */
public enum CountdownType implements CountdownEventType<CountdownType> {
    /**
     * test
     */
    TEST("test");

    String title;

    CountdownType(String title) {
        this.title = title;
    }

    @Override
    public String getType() {
        return title;
    }

    @Override
    public CountdownType eval(String type) {
        return TEST;
    }
}
