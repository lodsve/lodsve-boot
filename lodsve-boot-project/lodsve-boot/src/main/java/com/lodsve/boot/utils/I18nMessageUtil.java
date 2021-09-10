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
package com.lodsve.boot.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * 系统中获取i18n的工具类.
 *
 * @author 孙昊(Hulk)
 * @version V1.0, 15/8/14 上午11:57
 */
public class I18nMessageUtil implements MessageSourceAware {

    private static MessageSourceAccessor accessor;

    /**
     * 获取i18n文件中对应的国际化信息
     *
     * @param code   i18n文件中code
     * @param locale 地区信息
     * @param args   参数
     * @return 国际化信息
     */
    public static String getMessage(String code, Locale locale, Object... args) {
        if (locale == null) {
            return accessor.getMessage(code, args);
        }
        return accessor.getMessage(code, args, locale);
    }

    /**
     * 获取i18n文件中对应的国际化信息,如果不传locale信息，则从当前request获取，如果还是没有，则使用默认locale
     *
     * @param code i18n文件中code
     * @param args 参数
     * @return 国际化信息
     */
    public static String getMessage(String code, Object... args) {
        return accessor.getMessage(code, args);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        I18nMessageUtil.accessor = new MessageSourceAccessor(messageSource);
    }

}
