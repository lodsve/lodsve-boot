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
