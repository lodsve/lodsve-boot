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
import com.lodsve.boot.exception.LodsveBootException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 统一异常处理,这些异常说明是业务无法处理，区分statusCode供前端使用不同方式处理。
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ControllerAdvice(annotations = RestController.class)
public class LodsveBootExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(LodsveBootExceptionHandler.class);

    /**
     * 捕获自定义异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LodsveBootException.class)
    @ResponseBody
    public ResultSet<Object> handleException(final LodsveBootException exception) {
        if (logger.isErrorEnabled()) {
            logger.error(exception.getMessage(), exception);
        }
        int code = (null == exception.getCode()) ? HttpStatus.BAD_REQUEST.value() : exception.getCode();
        String message = StringUtils.isNotBlank(exception.getMessage()) ? exception.getMessage() : HttpStatus.BAD_REQUEST.getReasonPhrase();
        return ResultSet.error(null, code, message);
    }

    /**
     * 未被捕获的异常处理
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResultSet<Object> handleException(final Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(throwable.getMessage(), throwable);
        }

        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = StringUtils.isNotBlank(throwable.getMessage()) ? throwable.getMessage() : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        return ResultSet.error(null, code, message);

    }
}
