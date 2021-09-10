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
package com.lodsve.boot.redis.dynamic;

/**
 * 多数据源保存选择的数据源.<p/>
 * 手动使用时，切记要在使用完释放ThreadLocal. 使用方法如下(try-with-resources):<p/>
 * <pre>
 * try (RedisConnectionHolder dsh = RedisConnectionHolder.getInstance()) {
 *     dsh.set(dataSource);
 *     return point.proceed();
 * }
 * </pre>
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class RedisConnectionHolder {
    private static final ThreadLocal<String> REDIS_CONNECTION_FACTORY_NAME = new ThreadLocal<>();
    private static final RedisConnectionHolder INSTANCE = new RedisConnectionHolder();

    public static RedisConnectionHolder getInstance() {
        return INSTANCE;
    }

    public void set(String redisConnectionFactoryName) {
        REDIS_CONNECTION_FACTORY_NAME.set(redisConnectionFactoryName);
    }

    public String get() {
        return REDIS_CONNECTION_FACTORY_NAME.get();
    }

    public void release() {
        REDIS_CONNECTION_FACTORY_NAME.remove();
    }
}
