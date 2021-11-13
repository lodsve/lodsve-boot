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
package com.lodsve.boot.actuator.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * enums with description.
 *
 * @author Hulk Sun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnumDescription implements Serializable {
    private String shortName;
    private String name;
    private String description;
    private List<EnumDetail> details;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnumDetail implements Serializable {
        private String code;
        private String title;
    }
}
