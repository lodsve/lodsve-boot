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
package com.lodsve.boot.autoconfigure.encryption.source.aop;

import com.lodsve.boot.autoconfigure.encryption.resolver.EncryptablePropertyResolver;
import com.lodsve.boot.autoconfigure.encryption.source.CachingDelegateEncryptablePropertySource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.env.PropertySource;

import java.util.List;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class EncryptablePropertySourceMethodInterceptor<T> extends CachingDelegateEncryptablePropertySource<T> implements MethodInterceptor {

    public EncryptablePropertySourceMethodInterceptor(PropertySource<T> delegate, List<EncryptablePropertyResolver> resolvers) {
        super(delegate, resolvers);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (isRefreshCall(invocation)) {
            refresh();
            return null;
        }
        if (isGetDelegateCall(invocation)) {
            return getDelegate();
        }
        if (isGetPropertyCall(invocation)) {
            return getProperty(getNameArgument(invocation));
        }
        return invocation.proceed();
    }

    private String getNameArgument(MethodInvocation invocation) {
        return (String) invocation.getArguments()[0];
    }

    private boolean isGetDelegateCall(MethodInvocation invocation) {
        return "getDelegate".equals(invocation.getMethod().getName());
    }

    private boolean isRefreshCall(MethodInvocation invocation) {
        return "refresh".equals(invocation.getMethod().getName());
    }

    private boolean isGetPropertyCall(MethodInvocation invocation) {
        return "getProperty".equals(invocation.getMethod().getName())
            && invocation.getMethod().getParameters().length == 1
            && invocation.getMethod().getParameters()[0].getType() == String.class;
    }
}
