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
package com.lodsve.boot.webmvc.response;

import com.lodsve.boot.bean.ResultSet;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
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
public class ResultSetResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@Nonnull MethodParameter parameter, @Nonnull Class<? extends HttpMessageConverter<?>> converterClass) {
        Method method = parameter.getMethod();
        if (null == method) {
            return false;
        }
        IgnoreResult ignore = method.getAnnotation(IgnoreResult.class);
        if (null != ignore) {
            // 忽略
            return false;
        }

        Class<?> returnType = method.getReturnType();
        return !ResultSet.class.isAssignableFrom(returnType) && !ResultSet.class.isAssignableFrom(returnType) && !ResponseEntity.class.isAssignableFrom(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object object, @Nonnull MethodParameter parameter, @Nonnull MediaType mediaType,
                                  @Nonnull Class<? extends HttpMessageConverter<?>> converterClass, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        return ResultSet.ok(object);
    }
}
