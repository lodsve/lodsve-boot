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
package com.lodsve.boot.actuator.version;

import com.lodsve.boot.LodsveBootVersion;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.core.SpringVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * return [lodsve boot / spring framework / spring boot] versions.
 *
 * @author Hulk Sun
 */
@WebEndpoint(id = "versions")
public class LodsveBootVersionsEndpoint {
    private static final List<Versions> VERSIONS = new ArrayList<>(3);
    private static final String VERSION_NAME_LODSVE_BOOT = "lodsve-boot version";
    private static final String VERSION_NAME_SPRING = "springframework version";
    private static final String VERSION_NAME_SPRING_BOOT = "spring-boot version";

    @ReadOperation
    public List<Versions> versions() {
        if (VERSIONS.isEmpty()) {
            VERSIONS.add(new Versions(VERSION_NAME_SPRING, SpringVersion.getVersion()));
            VERSIONS.add(new Versions(VERSION_NAME_SPRING_BOOT, SpringBootVersion.getVersion()));
            VERSIONS.add(new Versions(VERSION_NAME_LODSVE_BOOT, LodsveBootVersion.getVersion()));
        }

        return VERSIONS;
    }
}
