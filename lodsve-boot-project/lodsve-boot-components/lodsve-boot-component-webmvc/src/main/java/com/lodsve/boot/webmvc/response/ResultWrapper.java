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
package com.lodsve.boot.webmvc.response;

import com.lodsve.boot.bean.WebResult;

import java.lang.annotation.*;

/**
 * 使用{@link WebResult}包装返回体.<p/>
 * 两种使用方式：<br/>
 * 1. 加在类头：表示整个类的所有api返回体都需要包装<br/>
 * 2. 加载方法上：表示该api需要包装<br/>
 * <p/>
 * 如果和{@link SkipWrapper} 共存，则优先认{@link SkipWrapper}.即忽略
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @see SkipWrapper
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResultWrapper {
}
