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
