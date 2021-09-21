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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.Serializable;

/**
 * 事件处理.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class CountdownEventListener implements ApplicationListener<CountdownEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CountdownEventListener.class);
    private final CountdownEventResolver eventResolver;

    public CountdownEventListener(CountdownEventResolver eventResolver) {
        this.eventResolver = eventResolver;
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(CountdownEvent event) {
        logger.debug("event is '{}'", event);
        Serializable key = event.getKey();
        CountdownEventType<?> type = event.getType();
        logger.debug("the [{}] [{}] is expired!", type, key);

        CountdownEventHandler handler = eventResolver.resolveEventHandler(type.getType());
        if (handler == null) {
            return;
        }

        handler.handler(key);
    }
}
