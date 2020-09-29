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
package com.lodsve.boot.bean;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 返回结果集.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 13-7-6 上午7:58
 */
@Data
public class ResultSet<T> implements Serializable {
    private static final String SUCCESS = "200";
    private static final String ERROR = "500";
    private static final String OK_MESSAGE = "Successful";
    private static final String ERROR_MESSAGE = "Some internal exceptions occurred";

    private Boolean result;
    private String code;
    private T data;
    private String message;

    public ResultSet<T> ok(T data) {
        ResultSet<T> result = new ResultSet<>();
        result.setCode(SUCCESS);
        result.setResult(true);
        result.setData(data);
        result.setMessage(OK_MESSAGE);

        return result;
    }

    public ResultSet<T> ok(T data, String message) {
        ResultSet<T> result = new ResultSet<>();
        result.setCode(SUCCESS);
        result.setResult(true);
        result.setData(data);
        result.setMessage(StringUtils.isBlank(message) ? OK_MESSAGE : message);

        return result;
    }

    public ResultSet<T> error() {
        ResultSet<T> result = new ResultSet<>();
        result.setCode(ERROR);
        result.setResult(false);
        result.setData(null);
        result.setMessage(ERROR_MESSAGE);

        return result;
    }

    public ResultSet<T> error(T data) {
        ResultSet<T> result = new ResultSet<>();
        result.setCode(ERROR);
        result.setResult(false);
        result.setData(data);
        result.setMessage(ERROR_MESSAGE);

        return result;
    }

    public ResultSet<T> error(T data, String code, String message) {
        ResultSet<T> result = new ResultSet<>();
        result.setCode(StringUtils.isBlank(code) ? ERROR : code);
        result.setResult(false);
        result.setData(data);
        result.setMessage(StringUtils.isBlank(message) ? ERROR_MESSAGE : message);

        return result;
    }
}
