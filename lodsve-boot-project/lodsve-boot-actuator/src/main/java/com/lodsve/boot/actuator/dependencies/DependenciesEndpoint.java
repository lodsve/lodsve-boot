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
package com.lodsve.boot.actuator.dependencies;

import com.lodsve.boot.actuator.dependencies.Dependencies.Dependency;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * read all Dependencies.
 *
 * @author Hulk Sun
 */
@WebEndpoint(id = "dependencies")
public class DependenciesEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(DependenciesEndpoint.class);
    private static final String DEFAULT_CLASSPATH_INDEX_PATH = "BOOT-INF/classpath.idx";
    private static final List<Dependency> DEPENDENCY_CACHE = new ArrayList<>(10);

    @ReadOperation
    public Dependencies loadDependencies() {
        if (DEPENDENCY_CACHE.isEmpty()) {
            // if run as java -jar xxx.jar -> load info from BOOT-INF/classpath.idx
            // if run as java -classpath ... -> load info via System.getProperties()
            List<Dependency> dependencies = loadDependenciesFromIdxFile();
            if (CollectionUtils.isEmpty(dependencies)) {
                dependencies = loadDependenciesFromClasspath();
            }

            DEPENDENCY_CACHE.addAll(dependencies);
        }

        return new Dependencies(DEPENDENCY_CACHE, DEPENDENCY_CACHE.size());
    }

    private List<Dependency> loadDependenciesFromClasspath() {
        String path = System.getProperty("java.class.path");
        String[] classpathArray = StringUtils.split(path, File.pathSeparator);
        return Arrays.stream(classpathArray).filter(classpath -> StringUtils.isNotBlank(classpath) && StringUtils.endsWith(classpath, ".jar")).map(this::parseClasspath).collect(Collectors.toList());
    }

    private Dependency parseClasspath(String classpath) {
        String[] temp = StringUtils.split(classpath, "/");
        classpath = temp[temp.length - 1];
        classpath = StringUtils.removeEnd(classpath, ".jar\"");

        return evalDependency(classpath);
    }

    private List<Dependency> loadDependenciesFromIdxFile() {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CLASSPATH_INDEX_PATH)) {
            if (null == in) {
                return Collections.emptyList();
            }
            List<String> lines = IOUtils.readLines(in);
            // line like [- "spring-boot-starter-2.3.4.RELEASE.jar"]
            return lines.stream().filter(StringUtils::isNotBlank).map(this::parseLine).collect(Collectors.toList());
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Cannot find BOOT-INF/classpath.idx in classpath! It maybe run as java -classpath!");
            }

            return Collections.emptyList();
        }
    }

    private Dependency parseLine(String line) {
        // line like [- "spring-boot-starter-2.3.4.RELEASE.jar"]
        // remove begin
        line = StringUtils.removeStart(line, "- \"");
        // remove end
        line = StringUtils.removeEnd(line, ".jar\"");

        return evalDependency(line);
    }

    private Dependency evalDependency(String nameAndVersion) {
        String[] nameAndVersionArray = StringUtils.split(nameAndVersion, "-");
        String version = StringUtils.removeEnd(nameAndVersionArray[nameAndVersionArray.length - 1], ".jar");
        String name = StringUtils.removeEnd(nameAndVersion, "-" + version + ".jar");
        return new Dependency(name, version);
    }
}
