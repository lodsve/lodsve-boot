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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 发起倒计时.
 *
 * @author Hulk Sun
 */
public class CountdownPublisher {
    private final RedisTemplate<Serializable, Serializable> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public CountdownPublisher(RedisTemplate<Serializable, Serializable> redisTemplate, ApplicationEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 保存到redis
     *
     * @param key  唯一标示
     * @param ttl  失效时长(单位:秒)
     * @param type 事件类型
     */
    public void countdown(Serializable key, int ttl, CountdownEventType<?> type) {
        Assert.notNull(key, "key不能为空!");
        Assert.notNull(type, "type不能为空!");

        if (ttl <= 0) {
            // 抛出事件
            eventPublisher.publishEvent(new CountdownEvent(this, key, type));
            return;
        }

        BoundValueOperations<Serializable, Serializable> operations = redisTemplate.boundValueOps(CountdownConstants.REDIS_KEY_PREFIX + type.getType() + CountdownConstants.REDIS_KEY_SEPARATOR + key);
        operations.set(key, ttl * 1000, TimeUnit.MILLISECONDS);
    }
}
