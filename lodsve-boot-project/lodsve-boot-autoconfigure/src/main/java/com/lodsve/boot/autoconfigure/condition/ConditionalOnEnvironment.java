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
 * 检查环境变量中是否有值.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @see OnEnvironmentCondition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnEnvironmentCondition.class)
public @interface ConditionalOnEnvironment {
    /**
     * {@link #name()}别名
     *
     * @return the names
     */
    String[] value() default {};

    /**
     * 应该应用于每个属性的前缀。 如果未指定，前缀会自动以点结尾。
     * 一个有效的前缀由一个或多个用点分隔的单词定义（例如 {@code "acme.system.feature"} ）。
     *
     * @return 前缀
     */
    String prefix() default "";

    /**
     * 要测试的属性的名称。 如果已定义前缀，则将其用于计算每个属性的完整键。
     * 例如，如果前缀为app.config且一个值为my-value ，则全键为app.config.my-value
     * 使用虚线符号指定每个属性，所有属性均使用小写字母“-”来分隔单词（
     * 例如， my-long-property ）。
     *
     * @return the names
     */
    String[] name() default {};

    /**
     * 属性期望值的字符串表示形式。 如果未指定，则该属性不得等于false
     *
     * @return 期望值
     */
    String[] havingValue() default "";

    /**
     * 指定如果未设置该属性，条件是否应匹配。 默认为false 。
     *
     * @return 如果该属性丢失，则应该匹
     */
    boolean matchIfMissing() default false;
}
