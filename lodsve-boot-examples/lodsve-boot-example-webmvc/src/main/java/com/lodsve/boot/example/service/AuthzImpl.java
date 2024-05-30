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

import com.lodsve.boot.component.security.core.Account;
import com.lodsve.boot.component.security.exception.AuthException;
import com.lodsve.boot.component.security.service.Authorization;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 */
@Component
public class AuthzImpl implements Authorization {
    public static boolean LOGIN = false;

    @Override
    public boolean isLogin(HttpServletRequest request) {
        return LOGIN;
    }

    @Override
    public void ifNotLogin(HttpServletRequest request, HttpServletResponse response) {
        throw new AuthException(105001, "no login!");
    }

    @Override
    public boolean authz(Account account, String... roles) {
        return "admin".equals(roles[0]);
    }

    @Override
    public void ifNotAuth(HttpServletRequest request, HttpServletResponse response, Account account) {
        throw new AuthException(105001, "no permission!");
    }

    @Override
    public Account getCurrentUser(HttpServletRequest request) {
        return new Account(1L, "sunhao");
    }
}
