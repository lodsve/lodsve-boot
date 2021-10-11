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
package com.lodsve.boot.io;

import com.lodsve.boot.io.template.BeetlTemplateResource;
import com.lodsve.boot.io.template.ThymeleafTemplateResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * Lodsve ResourceLoader.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2018-1-2-0002 20:51
 */
public class LodsveResourceLoader extends DefaultResourceLoader {
    public static final String RESOURCE_ZOOKEEPER_PREFIX = "zookeeper:";
    public static final String RESOURCE_BEETL_PREFIX = "beetl:";
    public static final String RESOURCE_THYMELEAF_PREFIX = "thymeleaf:";

    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (StringUtils.startsWith(location, RESOURCE_ZOOKEEPER_PREFIX)) {
            return new ZookeeperResource(StringUtils.substringAfter(location, RESOURCE_ZOOKEEPER_PREFIX));
        } else if (StringUtils.startsWith(location, RESOURCE_BEETL_PREFIX)) {
            return new BeetlTemplateResource(StringUtils.substringAfter(location, RESOURCE_BEETL_PREFIX), Collections.emptyMap());
        } else if (StringUtils.startsWith(location, RESOURCE_THYMELEAF_PREFIX)) {
            return new ThymeleafTemplateResource(StringUtils.substringAfter(location, RESOURCE_THYMELEAF_PREFIX), Collections.emptyMap());
        }

        return super.getResource(location);
    }
}
