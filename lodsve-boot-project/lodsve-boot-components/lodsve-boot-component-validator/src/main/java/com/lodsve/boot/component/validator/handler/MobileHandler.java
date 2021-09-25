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

import com.lodsve.boot.component.validator.annotations.Mobile;
import com.lodsve.boot.component.validator.exception.ErrorMessage;
import com.lodsve.boot.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 手机号码验证处理类.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class MobileHandler extends AbstractValidateHandler<Mobile> {
    private static final Logger logger = LoggerFactory.getLogger(MobileHandler.class);

    @Override
    protected ErrorMessage handle(Mobile annotation, Object value) {
        if (logger.isDebugEnabled()) {
            logger.debug("annotation is '{}', value is '{}'!", annotation, value);
        }

        return getMessage(Mobile.class, getClass(), "mobile-invalid", ValidateUtils.isMobile((String) value));
    }
}
