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

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;

/**
 * 根据操作系统的类型判断.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OnOperatingSystemCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnOperatingSystem.class.getName());
        Assert.notNull(attributes, "can't be null!");
        ConditionalOnOperatingSystem.OperatingSystem type = (ConditionalOnOperatingSystem.OperatingSystem) attributes.get("value");

        String os = System.getProperty("os.name").toLowerCase();
        String[] keyword = type.getKeyword();
        boolean match = true;
        for (String k : keyword) {
            match = match && os.contains(k);
        }

        return new ConditionOutcome(match, String.format("expected %s, but get %s!", Arrays.toString(keyword), os));
    }
}
