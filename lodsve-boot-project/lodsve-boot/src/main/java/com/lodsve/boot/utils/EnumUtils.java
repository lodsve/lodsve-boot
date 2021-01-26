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
package com.lodsve.boot.utils;

import com.lodsve.boot.bean.Codeable;
import org.springframework.util.Assert;

/**
 * 枚举相关工具类.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2018-11-26 22:55
 */
public class EnumUtils {
    private EnumUtils() {
    }

    /**
     * 根据枚举下标获取枚举值
     *
     * @param clazz   枚举类型
     * @param ordinal 下标
     * @return 枚举值
     */
    public static Enum<?> getEnumByOrdinal(Class<? extends Enum> clazz, Integer ordinal) {
        Assert.notNull(clazz, "enum class not be null!");

        Enum<?>[] enums = clazz.getEnumConstants();
        if (ordinal < 0 || ordinal >= enums.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }

        return enums[ordinal];
    }

    /**
     * 根据枚举名称获取枚举值
     *
     * @param clazz 枚举类型
     * @param name  枚举值
     * @param <T>   枚举类型
     * @return 枚举值
     */
    public static <T extends Enum<T>> T getEnumByName(Class<T> clazz, String name) {
        return Enum.valueOf(clazz, name);
    }

    /**
     * 根据枚举名称获取枚举值
     *
     * @param clazz 枚举类型
     * @param name  枚举值
     * @param <T>   枚举类型
     * @return 枚举值
     */
    public static <T extends Enum<T>> T evalByName(Class<T> clazz, String name) {
        return Enum.valueOf(clazz, name);
    }

    /**
     * 通过枚举的value来获取枚举项
     *
     * @param clazz 枚举类
     * @param value 枚举的value值
     * @param <T>   枚举类型
     * @return 枚举项
     */
    public static <T extends Codeable> T evalByValue(Class<T> clazz, String value) {
        Assert.notNull(value, "枚举值不能为空!");
        Assert.notNull(clazz, "枚举不能为空!");

        T[] constants = clazz.getEnumConstants();
        for (T constant : constants) {
            if (value.equals(constant.getCode())) {
                return constant;
            }
        }

        throw new RuntimeException(String.format("根据值[%s]在枚举类[%s]中找不到枚举项!", value, clazz.getName()));
    }

    /**
     * 通过枚举下标获取枚举项
     *
     * @param clazz   枚举类
     * @param ordinal 下标
     * @param <T>     枚举类型
     * @return 枚举项
     */
    public static <T extends Enum<?>> T evalByOrdinal(Class<T> clazz, int ordinal) {
        Assert.notNull(clazz, "枚举不能为空!");

        T[] constants = clazz.getEnumConstants();

        if (ordinal < 0 || ordinal >= constants.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return constants[ordinal];
    }
}
