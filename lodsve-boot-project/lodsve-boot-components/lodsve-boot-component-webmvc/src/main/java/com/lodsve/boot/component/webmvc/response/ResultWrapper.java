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
package com.lodsve.boot.component.webmvc.response;

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
