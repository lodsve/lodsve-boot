/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
