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
package com.lodsve.boot.actuator.autoconfigure.enums;

import com.lodsve.boot.actuator.enums.EnumsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * enum endpoint auto configuration.
 *
 * @author Hulk Sun
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnAvailableEndpoint(endpoint = EnumsEndpoint.class)
public class EnumsEndpointAutoConfiguration implements BeanFactoryAware {
    private static final Logger logger = LoggerFactory.getLogger(EnumsEndpointAutoConfiguration.class);
    private String[] basePackages;

    @Bean
    public EnumsEndpoint enumsEndpoint() {
        return new EnumsEndpoint(basePackages);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!AutoConfigurationPackages.has(beanFactory)) {
            logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
            return;
        }
        List<String> packages = AutoConfigurationPackages.get(beanFactory);
        if (logger.isDebugEnabled()) {
            packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
        }

        this.basePackages = packages.toArray(new String[0]);
    }
}
