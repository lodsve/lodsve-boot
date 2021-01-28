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

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * aop动态切换数据源.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Aspect
public class DynamicRedisConnectionFactoryAspect {
    @Around("@annotation(com.lodsve.boot.redis.dynamic.SwitchRedis)")
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
