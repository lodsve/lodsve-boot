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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;

/**
 * 封装HttpServletResponse
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class WebOutput {
    private final HttpServletResponse response;
    private final JsonConverter jsonConverter;

    /**
     * 构造函数
     *
     * @param response      response
     * @param jsonConverter jsonConverter
     */
    public WebOutput(HttpServletResponse response, JsonConverter jsonConverter) {
        this.response = response;
        this.jsonConverter = jsonConverter;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * 设置cookie
     *
     * @param name   name
     * @param value  value
     * @param maxAge 存活时间
     */
    public void setCookie(String name, String value, int maxAge) {
        Cookie c = new Cookie(name, value);
        if (maxAge > 0) {
            c.setMaxAge(maxAge);
        }
        c.setPath("/");
        this.response.addCookie(c);
    }

    public void setContentType(String contentType) {
        this.response.setContentType(contentType);
    }

    public void setContentType(String contextType, String charset) {
        if (charset == null) {
            this.response.setContentType(contextType);
        } else {
            this.response.setContentType(contextType + "; charset=" + charset);
        }
    }

    /**
     * 返回json数据
     *
     * @param params params
     * @throws Exception exception
     */
    public void toJson(Map<String, Object> params) throws Exception {
        this.response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        write(jsonConverter.toJson(params));
    }

    /**
     * 返回json数据
     *
     * @param params params
     * @param <T>    类型
     * @throws Exception exception
     */
    public <T extends Serializable> void toJson(T params) throws Exception {
        this.response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        write(jsonConverter.toJson(params));
    }

    public void write(String content) throws Exception {
        Writer out = response.getWriter();
        out.write(content);
    }
}
