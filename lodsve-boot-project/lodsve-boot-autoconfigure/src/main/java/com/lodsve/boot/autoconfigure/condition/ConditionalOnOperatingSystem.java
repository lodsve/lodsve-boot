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
package com.lodsve.boot.autoconfigure.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * 根据操作系统的类型判断.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnOperatingSystemCondition.class)
public @interface ConditionalOnOperatingSystem {
    /**
     * 操作系统类型
     *
     * @return 操作系统类型
     */
    OperatingSystem value();

    /**
     * 操作系统枚举.
     */
    enum OperatingSystem {
        /**
         * windows
         */
        Windows(new String[]{"windows"}),
        /**
         * linux
         */
        Linux(new String[]{"linux"}),
        /**
         * mac
         */
        Mac(new String[]{"mac", "os"});

        private final String[] keyword;

        OperatingSystem(String[] keyword) {
            this.keyword = keyword;
        }

        public String[] getKeyword() {
            return keyword;
        }
    }
}
