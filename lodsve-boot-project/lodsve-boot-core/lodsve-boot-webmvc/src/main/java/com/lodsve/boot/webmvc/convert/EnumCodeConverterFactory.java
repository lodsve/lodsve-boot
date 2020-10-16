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
package com.lodsve.boot.webmvc.convert;

import com.lodsve.boot.bean.Codeable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * spring mvc将传递的参数转换成枚举.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2014-12-17 20:23
 */
@SuppressWarnings({"all"})
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

        return new StringToEnum(targetType);
    }

    private static class StringToEnum<T extends Enum<T> & Codeable> implements Converter<String, T> {
        private final T[] enums;
        private final Class<T> enumType;

        private StringToEnum(Class<T> enumType) {
            this.enumType = enumType;
            this.enums = enumType.getEnumConstants();
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }

            T result;
            try {
                result = Enum.valueOf(enumType, source);
            } catch (Exception e) {
                result = null;
            }

            if (result != null) {
                return result;
            }

            for (T em : enums) {
                if (em.getCode().equals(source)) {
                    result = em;
                    break;
                }
            }

            return result;
        }
    }
}
