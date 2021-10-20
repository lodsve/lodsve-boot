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

import com.lodsve.boot.component.event.listener.EventListener;
import com.lodsve.boot.component.event.module.BaseEvent;
import com.lodsve.boot.context.ApplicationHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 事件执行器.
 *
 * @author Hulk Sun
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventExecutor {
    private static final Logger logger = LoggerFactory.getLogger(EventExecutor.class);

    private static final AtomicBoolean INIT_ALREADY = new AtomicBoolean(false);
    private final ExecutorService executorService;

    /**
     * 同步事件Map.
     */
    private static final Map<Class<? extends BaseEvent>, List<EventListener>> SYNC_EVENT_LISTENERS = new HashMap<>();

    /**
     * 异步事件Map.
     */
    private static final Map<Class<? extends BaseEvent>, List<EventListener>> ASYNC_EVENT_LISTENERS = new HashMap<>();
    /**
     * 事件class类型 --> 中文描述
     */
    private static final Map<Class<? extends BaseEvent>, String> OPERATION_EVENTS = new HashMap<>();

    private static final Object REGISTER_LOCK_OBJECT = new Object();

    public EventExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    private void init() {
        Collection<EventListener> eventListeners = ApplicationHelper.getInstance().getBeansByType(EventListener.class).values();
        eventListeners.forEach(this::initListener);

        INIT_ALREADY.set(true);
    }

    private void initListener(EventListener listener) {
        Type[] clazz = listener.getClass().getGenericInterfaces();
        if (0 == clazz.length) {
            if (logger.isWarnEnabled()) {
                logger.warn("listener {} has no event type!", listener);
            }
            return;
        }

        if (!(clazz[0] instanceof ParameterizedType)) {
            if (logger.isWarnEnabled()) {
                logger.warn("listener {} has no event type!", listener);
            }
            return;
        }
        Type type = ((ParameterizedType) clazz[0]).getActualTypeArguments()[0];
        Class<? extends BaseEvent> eventType;
        if (type instanceof Class) {
            eventType = (Class<? extends BaseEvent>) type;
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("listener {} has no event type!", listener);
            }
            return;
        }
        boolean isSync = listener.isSync();

        try {
            registerListener(eventType, listener, isSync);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 注册监听服务
     *
     * @param eventType 事件类型
     * @param listener  监听器
     * @param isSync    是否是同步执行
     */
    private void registerListener(Class<? extends BaseEvent> eventType, EventListener listener, boolean isSync) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        synchronized (REGISTER_LOCK_OBJECT) {
            if (eventType == null) {
                logger.warn("module types is null!");
                return;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("register listener '{}' for module type '{}'!", listener, eventType);
            }

            Map<Class<? extends BaseEvent>, List<EventListener>> eventListeners = isSync ? SYNC_EVENT_LISTENERS : ASYNC_EVENT_LISTENERS;

            List<EventListener> listeners = eventListeners.get(eventType);
            if (listeners == null) {
                listeners = new ArrayList<>();
            }

            listeners.add(listener);
            eventListeners.put(eventType, listeners);

            BaseEvent tmpEventObject = eventType.getDeclaredConstructor(Object.class).newInstance(new Object());
            String name = tmpEventObject.getName();
            OPERATION_EVENTS.put(eventType, StringUtils.isBlank(name) ? eventType.getSimpleName() : name);
        }
    }

    /**
     * 执行事件
     *
     * @param event 事件
     */
    protected <T extends BaseEvent> void executeEvent(T event) throws RuntimeException {
        // 初始化
        if (!INIT_ALREADY.get()) {
            init();
        }

        //1.先执行同步事件
        List<EventListener> syncListeners = SYNC_EVENT_LISTENERS.get(event.getClass());
        if (syncListeners != null && !syncListeners.isEmpty()) {
            execute(syncListeners, event);
        }

        //2.执行异步事件
        List<EventListener> asyncListeners = ASYNC_EVENT_LISTENERS.get(event.getClass());
        if (asyncListeners != null && !asyncListeners.isEmpty()) {
            executeAsyncEvent(asyncListeners, event);
        }
    }

    /**
     * 解析中文名
     *
     * @param event 事件
     * @return 中文名
     */
    protected String evalName(Class<? extends BaseEvent> event) {
        Assert.notNull(event, "event must not be null!");
        return OPERATION_EVENTS.get(event);
    }

    /**
     * 执行异步事件
     *
     * @param asyncListeners 异步事件监听
     * @param event          异步事件
     */
    private void executeAsyncEvent(final List<EventListener> asyncListeners, final BaseEvent event) throws RuntimeException {
        executorService.execute(() -> execute(asyncListeners, event));
    }

    private void execute(List<EventListener> listeners, BaseEvent event) {
        for (EventListener listener : listeners) {
            if (logger.isDebugEnabled()) {
                logger.debug("execute module '{}' use listener '{}'!", event, listener);
            }
            //执行
            listener.onEvent(event);
        }
    }
}
