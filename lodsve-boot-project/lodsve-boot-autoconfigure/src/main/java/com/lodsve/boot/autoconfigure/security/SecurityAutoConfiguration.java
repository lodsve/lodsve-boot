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
package com.lodsve.boot.autoconfigure.security;

import com.lodsve.boot.component.security.core.AuthzInterceptor;
import com.lodsve.boot.component.security.service.Authorization;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * config lodsve security.
 *
 * @author Hulk Sun
 */
@ConditionalOnClass(AuthzInterceptor.class)
@ConditionalOnBean(Authorization.class)
@Configuration
public class SecurityAutoConfiguration {
    @Bean
    public SecurityWebMvcConfigurer securityWebMvcConfigurer(ObjectProvider<Authorization> authorizationProvider) {
        return new SecurityWebMvcConfigurer(authorizationProvider.getIfUnique());
    }
}
