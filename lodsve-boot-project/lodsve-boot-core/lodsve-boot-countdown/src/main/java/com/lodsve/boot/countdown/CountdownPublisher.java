/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.lodsve.boot.countdown;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 发起倒计时.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
