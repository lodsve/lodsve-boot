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

import com.lodsve.boot.countdown.CountdownEventHandler;
import com.lodsve.boot.countdown.CountdownEventType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/12 下午9:42
 */
@Component
public class CountdownTestHandler implements CountdownEventHandler {

    @Override
    public void handler(Serializable key) {
        System.out.println(getEventType().getType() + "===========" + key);
    }

    @Override
    public Serializable resolveKey(String message) {
        return message;
    }

    @Override
    public CountdownEventType<CountdownType> getEventType() {
        return CountdownType.TEST;
    }
}
