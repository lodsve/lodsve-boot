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

import com.lodsve.boot.component.validator.annotations.Chinese;
import com.lodsve.boot.component.validator.exception.ErrorMessage;
import com.lodsve.boot.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中文验证的处理类.
 *
 * @author Hulk Sun
 */
public class ChineseHandler extends AbstractValidateHandler<Chinese> {
    private static final Logger logger = LoggerFactory.getLogger(ChineseHandler.class);

    @Override
    public ErrorMessage handle(Chinese annotation, Object value) {
        int min = annotation.min();
        int max = annotation.max();

        boolean result;
        if (min >= max) {
            logger.debug("no length limit!");
            result = ValidateUtils.isChinese((String) value);
            return getMessage(Chinese.class, this.getClass(), "chinese-invalid", result);
        } else {
            int length = ((String) value).length();
            logger.debug("get validate min is '{}', max is '{}', value length is '{}'", min, max, length);
            result = min <= length && max >= length && ValidateUtils.isChinese((String) value);
            return getMessage(Chinese.class, this.getClass(), "chinese-length", result, min, max, length);
        }
    }
}
