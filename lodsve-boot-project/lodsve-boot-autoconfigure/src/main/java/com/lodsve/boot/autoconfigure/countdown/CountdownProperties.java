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
package com.lodsve.boot.autoconfigure.countdown;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * .
 *
 * @author Hulk Sun
 */
@ConfigurationProperties("lodsve.countdown")
public class CountdownProperties {
    /**
     * 需要监听的redis数据库
     */
    private List<Integer> database;

    public List<Integer> getDatabase() {
        return database;
    }

    public void setDatabase(List<Integer> database) {
        this.database = database;
    }
}
