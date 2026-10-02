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
package com.lodsve.boot.component.webmvc.convert;

import com.lodsve.boot.bean.Codeable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * spring mvc将传递的参数转换成枚举.
 *
 * @author Hulk Sun
 */
public class EnumCodeConverterFactory implements ConverterFactory<String, Enum<? extends Codeable>>, ConditionalConverter {
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> clazz = targetType.getType();
        return Enum.class.isAssignableFrom(clazz) && Codeable.class.isAssignableFrom(clazz);
    }

    @Override
    public <T extends Enum<? extends Codeable>> Converter<String, T> getConverter(Class<T> targetType) {
        if (!Codeable.class.isAssignableFrom(targetType)) {
            return null;
        }

        return new StringToEnum<>(targetType);
    }

    private static class StringToEnum<T extends Enum<? extends Codeable>> implements Converter<String, T> {
        private final T[] enums;

        private StringToEnum(Class<T> enumType) {
            this.enums = enumType.getEnumConstants();
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }

            for (T em : enums) {
                if (em.name().equals(source)) {
                    return em;
                }
            }

            for (T em : enums) {
                if (Codeable.class.cast(em).getCode().equals(source)) {
                    return em;
                }
            }

            return null;
        }
    }
}
