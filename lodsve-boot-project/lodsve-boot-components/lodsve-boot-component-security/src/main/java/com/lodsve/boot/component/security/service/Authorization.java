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
package com.lodsve.boot.component.security.service;

import com.lodsve.boot.component.security.core.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 鉴权.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public interface Authorization {
    /**
     * 是否登录
     *
     * @param request 请求上下文
     * @return is logged
     */
    boolean isLogin(HttpServletRequest request);

    /**
     * 没有登录
     *
     * @param request  request
     * @param response response
     */
    void ifNotLogin(HttpServletRequest request, HttpServletResponse response);

    /**
     * 判断当前登录人是否含有给定的角色
     *
     * @param account 当前登录人
     * @param roles   允许放行的角色
     * @return true:鉴权成功  false:鉴权失败
     */
    boolean authz(Account account, String... roles);

    /**
     * 没有权限
     *
     * @param request  request
     * @param response response
     * @param account  登录用户
     */
    void ifNotAuth(HttpServletRequest request, HttpServletResponse response, Account account);

    /**
     * 获取当前登录人的ID
     *
     * @param request 请求上下文
     * @return 当前登录人ID
     */
    Account getCurrentUser(HttpServletRequest request);
}
