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
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 打印请求参数.
 *
 * @author Hulk Sun
 */
@Aspect
public class DebugRequestAspect {
    private static final Logger logger = LoggerFactory.getLogger(DebugRequestAspect.class);
    private static final String COMMA = ",";
    private static final List<Pattern> PATTERNS = new ArrayList<>(16);
    private static final List<String> DEFAULT_EXCLUDE_URL = Lists.newArrayList(
        ".*/v2/api-docs",
        ".*/swagger-resources",
        ".*/configuration/ui",
        ".*/webjars/.*",
        ".*/swagger-ui/",
        ".*/swagger-ui/index.html"
    );

    private final ObjectMapper objectMapper;
    private final List<String> excludeUrl;
    private final List<String> excludeAddress;

    public DebugRequestAspect(ObjectMapper objectMapper, List<String> excludeUrl, List<String> excludeAddress) {
        this.objectMapper = (null == objectMapper ? new ObjectMapper() : objectMapper);
        this.excludeUrl = excludeUrl;
        this.excludeAddress = excludeAddress;

        initPattern();
    }

    private void initPattern() {
        excludeUrl.addAll(DEFAULT_EXCLUDE_URL);
        for (String url : excludeUrl) {
            PATTERNS.add(Pattern.compile(url));
        }
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object deBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        if (!checkUrl(request)) {
            return joinPoint.proceed();
        }

        try {
            before(joinPoint);
        } catch (RuntimeException exception) {
            logger.warn("请求日志记录失败", exception);
        }

        StopWatch watch = new StopWatch();
        watch.start("");
        Object object = joinPoint.proceed();
        watch.stop();

        try {
            after(object, watch.getTotalTimeMillis());
        } catch (RuntimeException exception) {
            logger.warn("响应日志记录失败", exception);
        }

        return object;
    }

    private boolean checkUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        String client = request.getRemoteHost();

        if (excludeAddress.contains(client)) {
            return false;
        }
        for (Pattern pattern : PATTERNS) {
            if (pattern.matcher(url).matches()) {
                return false;
            }
        }

        return true;
    }

    private void before(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> names = request.getHeaderNames();

        List<String> header = Lists.newArrayList();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            String headerValue = request.getHeader(name);
            header.add(escapeLogValue(name) + " = " + (headerValue == null ? "null" : escapeLogValue(headerValue)));
        }

        if (logger.isInfoEnabled()) {
            String message = "\n请求相关信息：\n【请求头信息】->【{}】,\n【请求方法】->【{}】,\n【请求参数】->【{}】";
            Object[] args = joinPoint.getArgs();
            List<String> toString = Lists.newArrayList();
            for (Object arg : args) {
                toString.add(formatValue(arg));
            }

            logger.info(message, StringUtils.join(header, COMMA), joinPoint.getSignature(), toString);
        }
    }

    private void after(Object object, long totalTimeMillis) {
        if (logger.isInfoEnabled()) {
            String message = "\n执行情况：\n执行时间为：【{}毫秒】\n返回值为：【{}】";
            logger.info(message, totalTimeMillis, object == null ? "no return" : formatValue(object));
        }
    }

    String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof CharSequence || value instanceof Number || value instanceof Boolean
            || value instanceof Enum<?>) {
            return escapeLogValue(String.valueOf(value));
        }
        if (value instanceof HttpServletRequest) {
            return escapeLogValue(value.getClass().getName());
        }
        try {
            return escapeLogValue(objectMapper.writeValueAsString(value));
        } catch (Exception exception) {
            return "<无法序列化: " + value.getClass().getName() + ">";
        }
    }

    private String escapeLogValue(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                case '\u2028':
                    escaped.append("\\u2028");
                    break;
                case '\u2029':
                    escaped.append("\\u2029");
                    break;
                default:
                    if (Character.isISOControl(character)) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
            }
        }
        return escaped.toString();
    }
}
