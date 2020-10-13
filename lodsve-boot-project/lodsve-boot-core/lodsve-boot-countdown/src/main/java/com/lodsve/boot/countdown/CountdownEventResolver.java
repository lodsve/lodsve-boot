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
package com.lodsve.boot.countdown;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.ObjectProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据类名解析处理类.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 15/9/29 下午12:53
 */
public class CountdownEventResolver {
    private final static Map<String, CountdownEventHandler> HANDLERS = new HashMap<>();

    public CountdownEventResolver(ObjectProvider<List<CountdownEventHandler>> provider) {
        List<CountdownEventHandler> handlers = provider.getIfAvailable();
        if (CollectionUtils.isEmpty(handlers)) {
            return;
        }

        for (CountdownEventHandler bean : handlers) {
            CountdownEventType<?> type = bean.getEventType();

            CountdownEventResolver.HANDLERS.put(type.getType(), bean);
        }
    }

    public CountdownEventHandler resolveEventHandler(String type) {
        return HANDLERS.get(type);
    }
}
