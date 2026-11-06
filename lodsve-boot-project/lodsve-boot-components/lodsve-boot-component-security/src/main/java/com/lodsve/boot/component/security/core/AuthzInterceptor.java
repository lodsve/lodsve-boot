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
package com.lodsve.boot.component.security.core;

import com.lodsve.boot.component.security.annotation.Authz;
import com.lodsve.boot.component.security.service.Authorization;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 鉴权的拦截器.
 *
 * @author Hulk Sun
 */
public class AuthzInterceptor implements HandlerInterceptor {
    private final Authorization authorization;

    public AuthzInterceptor(Authorization authorization) {
        this.authorization = authorization;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //获取注解
        Authz ac = method.getAnnotation(Authz.class);

        if (ac == null) {
            return true;
        }

        //判断是否有权限
        Account account = LoginAccountHolder.getCurrentAccount();
        if (account == null) {
            authorization.ifNotLogin(request, response);
        }

        if (authorization.authz(account, ac.value())) {
            return true;
        } else {
            authorization.ifNotAuth(request, response, account);
        }

        return true;
    }
}
