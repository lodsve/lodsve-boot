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
package com.lodsve.boot.example.service;

import com.lodsve.boot.example.domain.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:41
 */
@Service
public class UserService {
    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(User user) {
        // string
        redisTemplate.opsForValue().set("user-" + user.getId(), user);

        // set
        redisTemplate.opsForSet().add("user-set", user);

        // list
        redisTemplate.opsForList().leftPush("user-list", user);

        // hash
        redisTemplate.opsForHash().put("user-hash", user.getId() + "", user);

        // zset
        redisTemplate.opsForZSet().add("user-zset", user, user.getId());
    }

    public User loadFromString(Long id) {
        return (User) redisTemplate.opsForValue().get("user-" + id);
    }

    public User getFromSet() {
        return (User) redisTemplate.opsForSet().pop("user-set");
    }

    public User getFromList() {
        return (User) redisTemplate.opsForList().leftPop("user-list");
    }

    public User getFromHash(Long id) {
        return (User) redisTemplate.opsForHash().get("user-hash", id + "");
    }

    public Set<User> getFromZset() {
        return redisTemplate.opsForZSet().range("user-zset", 0, 1).stream().map(u -> (User) u).collect(Collectors.toSet());
    }
}
