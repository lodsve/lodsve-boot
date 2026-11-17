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
package com.lodsve.boot.component.mongodb.dynamic;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Switches MongoDB data sources around annotated method invocations.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MongoDataSourceAspect {
    @Around("@annotation(switchMongoDataSource)")
    public Object around(ProceedingJoinPoint point, SwitchMongoDataSource switchMongoDataSource) throws Throwable {
        try (MongoDataSourceScope ignored = MongoDataSourceContextHolder.open(switchMongoDataSource.value())) {
            return point.proceed();
        }
    }
}
