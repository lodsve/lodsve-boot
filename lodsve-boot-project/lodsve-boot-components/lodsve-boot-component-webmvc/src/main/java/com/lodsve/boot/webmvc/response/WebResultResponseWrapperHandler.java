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
package com.lodsve.boot.webmvc.response;

import com.lodsve.boot.bean.WebResult;
import com.lodsve.boot.json.JsonConverter;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * 通用返回值处理.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@RestControllerAdvice(annotations = RestController.class)
public class WebResultResponseWrapperHandler implements ResponseBodyAdvice<Object> {
    private final JsonConverter jsonConverter;

    public WebResultResponseWrapperHandler(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public boolean supports(@Nonnull MethodParameter parameter, @Nonnull Class<? extends HttpMessageConverter<?>> converterClass) {
        Method method = parameter.getMethod();
        if (null == method) {
            return false;
        }
        if (checkReturnType(method.getReturnType())) {
            return false;
        }

        // 先判断该类头上是否有SkipWrapper，如果有，直接返回false
        SkipWrapper skip = method.getAnnotation(SkipWrapper.class);
        if (null != skip) {
            return false;
        }
        // 如果没有SkipWrapper，则判断方法上是否有ResultWrapper，如果有，返回true
        ResultWrapper wrapper = method.getAnnotation(ResultWrapper.class);
        if (null != wrapper) {
            return true;
        }
        // 如果方法上没有ResultWrapper，则判断类头上是否有ResultWrapper，如果没有，则返回false
        Class<?> controllerType = parameter.getContainingClass();
        ResultWrapper controllerWrapper = controllerType.getAnnotation(ResultWrapper.class);
        return null != controllerWrapper;
    }

    private boolean checkReturnType(Class<?> returnType) {
        return WebResult.class.isAssignableFrom(returnType) || ResponseEntity.class.isAssignableFrom(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object object, @Nonnull MethodParameter parameter, @Nonnull MediaType mediaType,
                                  @Nonnull Class<? extends HttpMessageConverter<?>> converterClass, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {

        if (object instanceof String || StringHttpMessageConverter.class.isAssignableFrom(converterClass)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return jsonConverter.toJson(WebResult.ok(object));
        }
        return WebResult.ok(object);
    }
}
