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
package com.lodsve.boot.component.webmvc.resolver;

import com.lodsve.boot.json.JsonConverter;
import com.lodsve.boot.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析加了注解{@link Bind}的参数.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2015-1-29 21:49
 */
public class BindDataHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final JsonConverter jsonConverter;

    public BindDataHandlerMethodArgumentResolver(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Bind bind = parameter.getParameterAnnotation(Bind.class);

        return bind != null && StringUtils.isNotEmpty(bind.value());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Bind bind = parameter.getParameterAnnotation(Bind.class);
        if (null == bind) {
            return null;
        }
        //前缀
        String name = bind.value();
        //类型
        Class<?> clazz = parameter.getParameterType();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (null == request) {
            return null;
        }

        //所有参数
        Enumeration<String> params = request.getParameterNames();
        Map<String, String> paramsMap = new HashMap<>(16);
        while (params.hasMoreElements()) {
            String key = params.nextElement();
            String value = request.getParameter(key);

            if (key.startsWith(name + ".")) {
                String field = StringUtils.removeStart(key, name + ".");
                paramsMap.put(field, value);
            }
        }

        //转成json
        String paramsJson = jsonConverter.toJson(paramsMap);

        WebInput in = new WebInput(request);
        Object normalValue = in.getBean(clazz);
        Object jacksonValue = jsonConverter.toObject(paramsJson, clazz);

        return ObjectUtils.mergerObject(jacksonValue, normalValue);
    }
}
