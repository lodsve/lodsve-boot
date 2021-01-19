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
