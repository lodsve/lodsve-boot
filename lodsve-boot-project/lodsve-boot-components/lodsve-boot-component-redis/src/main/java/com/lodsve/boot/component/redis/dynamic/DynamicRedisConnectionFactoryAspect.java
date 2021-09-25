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
package com.lodsve.boot.component.redis.dynamic;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * aop动态切换数据源.
 *
 * @author Hulk Sun
 */
@Aspect
public class DynamicRedisConnectionFactoryAspect {
    @Around("@annotation(com.lodsve.boot.component.redis.dynamic.SwitchRedis)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String dataSource = StringUtils.EMPTY;
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(SwitchRedis.class)) {
            SwitchRedis annotation = method.getAnnotation(SwitchRedis.class);
            // 取出注解中的数据源名
            dataSource = annotation.value();
        }

        RedisConnectionHolder holder = RedisConnectionHolder.getInstance();
        holder.set(dataSource);
        Object result = point.proceed();

        holder.release();
        return result;
    }
}
