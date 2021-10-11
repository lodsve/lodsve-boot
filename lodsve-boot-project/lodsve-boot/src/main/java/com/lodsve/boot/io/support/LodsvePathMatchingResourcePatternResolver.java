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
package com.lodsve.boot.io.support;

import com.lodsve.boot.io.LodsveResourceLoader;
import com.lodsve.boot.io.ZookeeperResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.lodsve.boot.io.LodsveResourceLoader.RESOURCE_ZOOKEEPER_PREFIX;

/**
 * 支持读取zookeeper里的数据，其他类型还是由{@link PathMatchingResourcePatternResolver}处理.
 *
 * @author Hulk Sun
 */
public class LodsvePathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {
    public LodsvePathMatchingResourcePatternResolver() {
        super(new LodsveResourceLoader());
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        Assert.notNull(locationPattern, "Location pattern must not be null");
        if (!StringUtils.startsWith(locationPattern, RESOURCE_ZOOKEEPER_PREFIX)) {
            return super.getResources(locationPattern);
        }

        int prefixEnd = locationPattern.indexOf(":") + 1;
        if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
            // a file pattern
            return findZookeeperPathMatchingResources(locationPattern);
        } else {
            // a single resource with the given name
            return new Resource[]{getResourceLoader().getResource(locationPattern)};
        }
    }

    private Resource[] findZookeeperPathMatchingResources(String locationPattern) throws IOException {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<>(16);
        for (Resource rootDirResource : rootDirResources) {
            if (!rootDirResource.exists()) {
                continue;
            }
            result.addAll(doFindPathMatchingZookeeperResources(rootDirResource, subPattern));
        }

        return result.toArray(new Resource[0]);
    }

    private Collection<? extends Resource> doFindPathMatchingZookeeperResources(Resource rootDirResource, String subPattern) {
        if (!(rootDirResource instanceof ZookeeperResource)) {
            return Collections.emptySet();
        }

        Set<Resource> result = new HashSet<>(16);
        String fullPattern = org.springframework.util.StringUtils.replace(((ZookeeperResource) rootDirResource).getPath(), File.separator, "/");
        if (!subPattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern = fullPattern + org.springframework.util.StringUtils.replace(subPattern, File.separator, "/");

        doRetrieveMatchingResources(fullPattern, rootDirResource, result);
        return result;
    }

    private void doRetrieveMatchingResources(String fullPattern, Resource content, Set<Resource> result) {
        ZookeeperResource resource = (ZookeeperResource) content;
        List<String> children = resource.listChildren();
        for (String child : children) {
            String childPath = resource.getPath() + "/" + child;
            ZookeeperResource childResource = new ZookeeperResource(childPath);
            String currPath = org.springframework.util.StringUtils.replace(childPath, File.separator, "/");

            if (childResource.isFolder() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
                doRetrieveMatchingResources(fullPattern, childResource, result);
            }

            if (getPathMatcher().match(fullPattern, currPath)) {
                result.add(childResource);
            }
        }
    }
}
