/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.lodsve.boot.filesystem.handler;

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
     */
    public abstract String resolveRealName(String objectName) throws FileSystemException;

}
