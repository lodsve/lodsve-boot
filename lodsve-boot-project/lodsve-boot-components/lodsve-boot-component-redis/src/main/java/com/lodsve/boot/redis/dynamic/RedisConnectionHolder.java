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
