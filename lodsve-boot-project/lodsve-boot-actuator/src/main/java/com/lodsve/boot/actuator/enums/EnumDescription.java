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

import java.io.Serializable;
import java.util.List;

/**
 * enums with description.
 *
 * @author Hulk Sun
 */
public class EnumDescription implements Serializable {
    private String shortName;
    private String name;
    private String description;
    private List<EnumDetail> details;

    public EnumDescription() {
    }

    public EnumDescription(String shortName, String name, String description, List<EnumDetail> details) {
        this.shortName = shortName;
        this.name = name;
        this.description = description;
        this.details = details;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EnumDetail> getDetails() {
        return details;
    }

    public void setDetails(List<EnumDetail> details) {
        this.details = details;
    }

    public static class EnumDetail implements Serializable {
        private String code;
        private String title;

        public EnumDetail() {
        }

        public EnumDetail(String code, String title) {
            this.code = code;
            this.title = title;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
