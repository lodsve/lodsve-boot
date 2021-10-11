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
package com.lodsve.boot.autoconfigure.event;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * event config properties for thread.
 *
 * @author Hulk Sun
 */
@Data
@ConfigurationProperties(prefix = "lodsve.event")
public class EventProperties {
    /**
     * core pool size
     */
    private int corePoolSize = 1;
    /**
     * max pool size
     */
    private int maxPoolSize = Integer.MAX_VALUE;
    /**
     * time to keep alive in second
     */
    private int keepAliveSeconds = 60;
    /**
     * is allow core thread timeout
     */
    private boolean allowCoreThreadTimeOut = false;
    /**
     * queue capacity
     */
    private int queueCapacity = Integer.MAX_VALUE;
    /**
     * is expose the Executor which is not configurable
     */
    private boolean exposeUnconfigurableExecutor = false;
}
