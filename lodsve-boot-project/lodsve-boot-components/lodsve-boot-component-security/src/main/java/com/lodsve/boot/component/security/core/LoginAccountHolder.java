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

/**
 * 在当前请求（即一个线程中）中保存当前登录者.
 *
 * @author Hulk Sun
 */
public class LoginAccountHolder {
    private static final ThreadLocal<Account> ACCOUNT_THREAD_LOCAL = new ThreadLocal<>();

    public static Account getCurrentAccount() {
        return ACCOUNT_THREAD_LOCAL.get();
    }

    public static void setCurrentAccount(Account account) {
        ACCOUNT_THREAD_LOCAL.set(account);
    }

    public static void removeCurrentAccount() {
        ACCOUNT_THREAD_LOCAL.remove();
    }
}
