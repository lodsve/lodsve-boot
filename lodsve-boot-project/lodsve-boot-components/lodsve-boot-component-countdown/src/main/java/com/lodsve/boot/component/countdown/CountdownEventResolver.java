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
package com.lodsve.boot.component.countdown;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.ObjectProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据类名解析处理类.
 *
 * @author Hulk Sun
 */
public class CountdownEventResolver {
    private static final Map<String, CountdownEventHandler> HANDLERS = new HashMap<>();

    public CountdownEventResolver(ObjectProvider<List<CountdownEventHandler>> provider) {
        List<CountdownEventHandler> handlers = provider.getIfAvailable();
        if (CollectionUtils.isEmpty(handlers)) {
            return;
        }

        for (CountdownEventHandler bean : handlers) {
            CountdownEventType<?> type = bean.getEventType();

            CountdownEventResolver.HANDLERS.put(type.getType(), bean);
        }
    }

    public CountdownEventHandler resolveEventHandler(String type) {
        return HANDLERS.get(type);
    }
}
