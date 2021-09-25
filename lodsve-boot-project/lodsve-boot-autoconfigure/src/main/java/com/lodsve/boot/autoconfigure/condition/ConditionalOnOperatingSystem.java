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
package com.lodsve.boot.autoconfigure.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 根据操作系统的类型判断.
 *
 * @author Hulk Sun
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
