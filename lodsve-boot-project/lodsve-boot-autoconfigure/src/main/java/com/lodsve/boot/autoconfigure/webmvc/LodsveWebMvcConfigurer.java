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
package com.lodsve.boot.autoconfigure.webmvc;

import com.lodsve.boot.json.JsonConverter;
import com.lodsve.boot.webmvc.convert.EnumCodeConverterFactory;
import com.lodsve.boot.webmvc.convert.StringDateConvertFactory;
import com.lodsve.boot.webmvc.resolver.BindDataHandlerMethodArgumentResolver;
import com.lodsve.boot.webmvc.resolver.WebResourceDataHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * spring mvc 配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class LodsveWebMvcConfigurer implements WebMvcConfigurer {
    private final JsonConverter jsonConverter;

    public LodsveWebMvcConfigurer(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        BindDataHandlerMethodArgumentResolver bindDataHandlerMethodArgumentResolver = new BindDataHandlerMethodArgumentResolver(jsonConverter);
        WebResourceDataHandlerMethodArgumentResolver webResourceDataHandlerMethodArgumentResolver = new WebResourceDataHandlerMethodArgumentResolver(jsonConverter);

        argumentResolvers.add(bindDataHandlerMethodArgumentResolver);
        argumentResolvers.add(webResourceDataHandlerMethodArgumentResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new EnumCodeConverterFactory());
        registry.addConverterFactory(new StringDateConvertFactory());
    }
}
