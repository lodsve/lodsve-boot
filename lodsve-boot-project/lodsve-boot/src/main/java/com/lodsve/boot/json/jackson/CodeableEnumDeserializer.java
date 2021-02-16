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
package com.lodsve.boot.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lodsve.boot.bean.Codeable;
import com.lodsve.boot.utils.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Jackson反序列化枚举时，将code或者枚举value变成枚举.<br/>
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2016/11/3 下午2:56
 */
@SuppressWarnings("all")
public class CodeableEnumDeserializer extends JsonDeserializer<Enum> {
    @Override
    public Enum deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String value = p.getValueAsString();
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Class<? extends Enum> clazz = getType(p);
        if (clazz == null) {
            return null;
        }

        if (!Codeable.class.isAssignableFrom(clazz)) {
            if (NumberUtils.isCreatable(value)) {
                return EnumUtils.evalByOrdinal(clazz, NumberUtils.createInteger(value));
            } else {
                return Enum.valueOf(clazz, value);
            }
        }

        // 优先根据codeable去获取
        return getEnumFromCodeable(clazz, value);
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> Class<T> getType(JsonParser p) throws IOException {
        Object object = p.getCurrentValue();
        Class<?> clazz = object.getClass();
        String fieldName = p.getCurrentName();

        Field field = ReflectionUtils.findField(clazz, fieldName);
        Class<?> type = ((null != field) ? field.getType() : null);

        if (support(type)) {
            return (Class<T>) type;
        }

        return null;
    }

    private boolean support(Class<?> clazz) {
        return clazz != null && Enum.class.isAssignableFrom(clazz) && Enum.class.isAssignableFrom(clazz);
    }

    private Enum<?> getEnumFromCodeable(Class<? extends Enum> clazz, String value) {
        for (Enum<?> em : clazz.getEnumConstants()) {
            Codeable codeable = (Codeable) em;
            if (codeable.getCode().equals(value)) {
                return em;
            }
        }

        return null;
    }
}