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
package com.lodsve.boot.example.countdown;

import com.lodsve.boot.countdown.CountdownEventType;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/12 下午9:38
 */
public enum CountdownType implements CountdownEventType<CountdownType> {
    /**
     * test
     */
    TEST("test");

    String title;

    CountdownType(String title) {
        this.title = title;
    }

    @Override
    public String getType() {
        return title;
    }

    @Override
    public CountdownType eval(String type) {
        return TEST;
    }
}
