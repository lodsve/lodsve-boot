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
package com.lodsve.boot.example.enums;

import com.lodsve.boot.bean.Codeable;

/**
 * 用户状态枚举.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 15/8/10 上午11:45
 */
public enum Status implements Codeable {
    ENABLED("100", "启用"), DISABLED("101", "禁用"), AUDITING("102", "审核中"), DELETED("103", "已删除");

    private String code;
    private String title;

    Status(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
