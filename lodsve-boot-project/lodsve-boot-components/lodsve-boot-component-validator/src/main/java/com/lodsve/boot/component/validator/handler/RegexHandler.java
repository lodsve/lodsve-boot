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
package com.lodsve.boot.component.validator.handler;

import com.lodsve.boot.component.validator.annotations.Regex;
import com.lodsve.boot.component.validator.exception.ErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式验证处理类.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class RegexHandler extends AbstractValidateHandler<Regex> {
    private static final Logger logger = LoggerFactory.getLogger(RegexHandler.class);

    @Override
    protected ErrorMessage handle(Regex annotation, Object value) {
        if (!(value instanceof String)) {
            logger.error("is not string!");
            return getMessage(Regex.class, getClass(), "regex-error", false);
        }
        String regexPattern = annotation.regex();
        if (StringUtils.isEmpty(regexPattern)) {
            logger.error("given empty regex string!");
            throw new RuntimeException("regex pattern string is null!");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("the regex pattern is '{}'!", regexPattern);
        }

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher((String) value);

        return getMessage(Regex.class, getClass(), "regex-not-match", matcher.matches(), annotation, value);
    }
}
