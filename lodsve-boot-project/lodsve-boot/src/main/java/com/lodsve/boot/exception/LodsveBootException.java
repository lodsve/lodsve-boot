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
package com.lodsve.boot.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * 系统中其他所有的异常均需要继承.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 15/8/14 下午12:27
 */
public class LodsveBootException extends NestedRuntimeException {
    private static final Integer BAD_REQUEST_CODE = 400;
    /**
     * 异常code
     */
    private final Integer code;

    public LodsveBootException(String message) {
        super(message);
        this.code = BAD_REQUEST_CODE;
    }

    /**
     * @param code    异常编码，在i18n配置文件中配置的编码，请确保该异常编码已经定义
     * @param message 后台异常内容，这个内容主要用于输出后台日志，便于异常诊断
     */
    public LodsveBootException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
