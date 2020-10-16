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
package com.lodsve.boot.webmvc.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;

/**
 * 封装HttpServletResponse
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2012年1月19日
 */
public class WebOutput {
    private final HttpServletResponse response;
    private final ObjectMapper objectMapper;

    /**
     * 构造函数
     *
     * @param response
     * @param objectMapper
     */
    public WebOutput(HttpServletResponse response, ObjectMapper objectMapper) {
        this.response = response;
        this.objectMapper = objectMapper;
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
     * @param params
     */
    public void toJson(Map<String, Object> params) throws Exception {
        this.response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        write(objectMapper.writeValueAsString(params));
    }

    /**
     * 返回json数据
     *
     * @param params
     */
    public <T extends Serializable> void toJson(T params) throws Exception {
        this.response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        write(objectMapper.writeValueAsString(params));
    }

    public void write(String content) throws Exception {
        Writer out = response.getWriter();
        out.write(content);
    }
}
