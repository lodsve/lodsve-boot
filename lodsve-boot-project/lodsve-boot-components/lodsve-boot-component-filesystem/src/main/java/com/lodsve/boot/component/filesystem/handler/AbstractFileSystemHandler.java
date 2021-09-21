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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;

/**
 * 公共.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public abstract class AbstractFileSystemHandler implements FileSystemHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractFileSystemHandler.class);

    @Override
    public String downloadFileForStream(String objectName, String targetDir) throws IOException {
        File tempFolder = createTempFolder(targetDir);

        if (logger.isInfoEnabled()) {
            logger.info("download file : " + tempFolder.getAbsolutePath() + "/" + objectName);
        }

        // 获取文件真实名称
        String realName = resolveRealName(objectName);

        File fileTemp = new File(tempFolder, realName);
        File parentFolder = new File(fileTemp.getParent());
        if (!parentFolder.exists()) {
            if (!parentFolder.mkdirs()) {
                throw new FileSystemException("file system operation fails: create folder[" + fileTemp.getParent() + "] error!");
            }
        }

        download(objectName, fileTemp);
        return fileTemp.getAbsolutePath();
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

    /**
     * 下载文件到指定位置
     *
     * @param objectName   需要下载的文件
     * @param downloadFile 指定位置
     */
    public abstract void download(String objectName, File downloadFile);

    /**
     * 解析文件真实名称
     *
     * @param objectName 需要下载的文件
     * @return 文件真实名称
     * @throws FileSystemException Object Metadata is NULL
     */
    public abstract String resolveRealName(String objectName) throws FileSystemException;

}
