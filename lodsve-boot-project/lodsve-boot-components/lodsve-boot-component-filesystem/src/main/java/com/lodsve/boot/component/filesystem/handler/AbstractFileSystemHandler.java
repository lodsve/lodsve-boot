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
package com.lodsve.boot.component.filesystem.handler;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公共.
 *
 * @author Hulk Sun
 */
public abstract class AbstractFileSystemHandler implements FileSystemHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractFileSystemHandler.class);
    private static final Map<String, Boolean> BUCKET_ACCESS_CONTROL = new HashMap<>(16);
    private static final String[] URL_PREFIX = new String[]{"http://", "https://"};

    protected final Long defaultExpire;
    protected final String endpoint;

    public AbstractFileSystemHandler(String endpoint, Long defaultExpire, Map<String, Boolean> bucketAcl) {
        this.defaultExpire = (null == defaultExpire ? 10 * 60 * 1000L : defaultExpire);
        if (StringUtils.containsAny(endpoint, URL_PREFIX)) {
            this.endpoint = endpoint;
        } else {
            this.endpoint = "http://" + endpoint;
        }
        BUCKET_ACCESS_CONTROL.putAll(MapUtils.isEmpty(bucketAcl) ? new HashMap<>(16) : bucketAcl);
    }

    @Override
    public String downloadFile(String bucketName, String objectName, String targetDir) throws IOException {
        Assert.hasText(bucketName, "bucket name can't be null!");
        File tempFolder = createTempFolder(targetDir);

        if (logger.isInfoEnabled()) {
            logger.info("download file : " + tempFolder.getAbsolutePath() + "/" + objectName);
        }

        // 获取文件真实名称
        String realName = getRealFileName(bucketName, objectName);

        File fileTemp = new File(tempFolder, realName);
        File parentFolder = new File(fileTemp.getParent());
        if (!parentFolder.exists()) {
            if (!parentFolder.mkdirs()) {
                throw new FileSystemException("file system operation fails: create folder[" + fileTemp.getParent() + "] error!");
            }
        }

        download(bucketName, objectName, fileTemp);
        return fileTemp.getAbsolutePath();
    }

    @Override
    public InputStream downloadStream(String bucketName, String objectName) throws IOException {
        Assert.hasText(bucketName, "bucket name can't be null!");
        return download(bucketName, objectName);
    }

    /**
     * 创建临时目录
     *
     * @param tempFolderPath 临时目录
     * @return 临时目录
     */
    public File createTempFolder(String tempFolderPath) {
        File tempFolder = new File(tempFolderPath);
        if (!tempFolder.exists()) {
            if (!tempFolder.mkdirs()) {
                // 系统临时文件夹
                tempFolder = new File(System.getProperty("java.io.tmpdir"));
                logger.error("创建临时文件夹失败！");
            }
        }

        return tempFolder;
    }

    @Override
    public Map<String, String> getUrls(String bucketName, List<String> objectNames) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        return objectNames.stream().collect(Collectors.toMap(ele -> ele, value -> getUrl(bucketName, value), (ele1, ele2) -> ele2));
    }

    @Override
    public Map<String, String> getUrls(String bucketName, List<String> objectNames, Long expireTime) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        return objectNames.stream().collect(Collectors.toMap(ele -> ele, ele -> getUrl(bucketName, ele, expireTime), (ele1, ele2) -> ele2));
    }

    /**
     * 判断存储桶是否是公开桶
     *
     * @param bucketName 桶的名称
     * @return 是否是公开桶
     */
    protected Boolean isPublic(String bucketName) {
        Boolean isPublic = BUCKET_ACCESS_CONTROL.get(bucketName);
        return null != isPublic && isPublic;
    }

    /**
     * 下载文件到指定位置
     *
     * @param bucketName   桶的名称
     * @param objectName   需要下载的文件
     * @param downloadFile 指定位置
     */
    public abstract void download(String bucketName, String objectName, File downloadFile);

    /**
     * 下载文件到输入流
     *
     * @param bucketName 桶的名称
     * @param objectName 需要下载的文件
     * @return 文件输入流
     * @throws FileSystemException file not exist!
     */
    public abstract InputStream download(String bucketName, String objectName) throws FileSystemException;

}
