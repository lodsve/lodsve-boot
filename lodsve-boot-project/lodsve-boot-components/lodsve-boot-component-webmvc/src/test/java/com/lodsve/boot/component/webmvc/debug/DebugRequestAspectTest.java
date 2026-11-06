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
package com.lodsve.boot.component.webmvc.debug;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;

public class DebugRequestAspectTest {
    @Test
    public void nullAndStringArgumentsDoNotInterruptRequest() throws Throwable {
        DebugRequestAspect aspect = new DebugRequestAspect(new ObjectMapper(), new ArrayList<>(), new ArrayList<>());
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class<?>[]{HttpServletRequest.class}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "getRequestURI":
                        return "/api/callbacks/wechat-refund";
                    case "getRemoteHost":
                        return "127.0.0.1";
                    case "getHeaderNames":
                        return Collections.emptyEnumeration();
                    default:
                        return null;
                }
            });
        Signature signature = (Signature) Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class<?>[]{Signature.class}, (proxy, method, args) -> "refund");
        ProceedingJoinPoint joinPoint = (ProceedingJoinPoint) Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class<?>[]{ProceedingJoinPoint.class}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "getArgs":
                        return new Object[]{null, "payload"};
                    case "getSignature":
                        return signature;
                    case "proceed":
                        return "ok";
                    default:
                        return null;
                }
            });
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        try {
            Assert.assertEquals("ok", aspect.deBefore(joinPoint));
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Test
    public void unserializableValueDoesNotThrow() {
        DebugRequestAspect aspect = new DebugRequestAspect(new ObjectMapper(), new ArrayList<>(), new ArrayList<>());
        Assert.assertEquals("null", aspect.formatValue(null));
        Assert.assertEquals("payload", aspect.formatValue("payload"));
        Assert.assertTrue(aspect.formatValue(new Object()).startsWith("<无法序列化:"));
    }

    @Test
    public void lineBreaksInValuesAreEscapedBeforeLogging() {
        DebugRequestAspect aspect = new DebugRequestAspect(new ObjectMapper(), new ArrayList<>(), new ArrayList<>());

        String input = "first\r\nsecond\tthird\u2028fourth";
        Assert.assertEquals("first\\r\\nsecond\\tthird\\u2028fourth", aspect.formatValue(input));
    }
}
