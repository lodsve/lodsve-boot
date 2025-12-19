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
package com.lodsve.boot.component.event.module;

import java.util.EventObject;

/**
 * 事件的基类.
 *
 * @author Hulk Sun
 */
public abstract class BaseEvent extends EventObject {
    /**
     * 事件发生时的系统时间戳.
     */
    private final long timestamp;

    /**
     * 构造函数.
     *
     * @param source 最初发生事件的对象
     * @throws IllegalArgumentException 如果 source 为 null
     */
    public BaseEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 获取事件发生的时间戳.
     *
     * @return 时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 获取事件显示名称.
     *
     * @return 事件显示名称
     */
    public abstract String getName();
}
