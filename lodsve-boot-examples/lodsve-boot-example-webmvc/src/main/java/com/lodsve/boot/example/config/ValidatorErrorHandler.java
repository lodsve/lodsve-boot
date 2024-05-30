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
package com.lodsve.boot.example.config;

import com.lodsve.boot.component.validator.exception.ErrorMessage;
import com.lodsve.boot.component.validator.exception.ExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Component
public class ValidatorErrorHandler extends ExceptionHandler {
    @Override
    public String getMessage(List<ErrorMessage> messages) {
        List<String> messageList = new ArrayList<>(messages.size());
        for (ErrorMessage em : messages) {
            messageList.add(em.getMessage());
        }

        return StringUtils.join(messageList, "####");
    }
}
