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
package com.lodsve.boot.component.event;

import com.lodsve.boot.component.event.module.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件发布器.
 *
 * @author Hulk Sun
 */
public class EventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    /**
     * 事件执行者
     */
    private final EventExecutor eventExecutor;

    public EventPublisher(EventExecutor eventExecutor) {
        this.eventExecutor = eventExecutor;
    }

    /**
     * 发布事件
     *
     * @param baseEvent event
     */
    public <T extends BaseEvent> void publish(T baseEvent) {
        if (logger.isDebugEnabled()) {
            logger.debug("****************execute module '{}' start!", baseEvent);
        }
        eventExecutor.executeEvent(baseEvent);
        if (logger.isDebugEnabled()) {
            logger.debug("****************execute module '{}' stop!", baseEvent);
        }
    }

    /**
     * 解析中文名
     *
     * @param event 事件
     * @return 中文名
     */
    public String evalName(Class<? extends BaseEvent> event) {
        return eventExecutor.evalName(event);
    }
}
