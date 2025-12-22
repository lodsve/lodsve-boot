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

import com.lodsve.boot.component.filesystem.bean.FileBean;
import com.lodsve.boot.component.filesystem.bean.FileSystemResult;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.List;
import java.util.Map;

/**
 * 文件上传、下载操作.
 *
 * @author Hulk Sun
 */
public interface FileSystemHandler extends DisposableBean {
    /**
     * 创建文件夹
     *
     * @param bucketName 桶的名称
     * @param folder     文件夹名如"qj_nanjing/"
     * @return 是否创建成功，如果文件夹已经存在，也会返回true，表示创建成功
     */
    boolean createFolder(String bucketName, String folder);

    /**
     * 文件上传
     *
     * @param file 上传文件
     * @return Result
     * @throws IOException 文件流异常
     */
    FileSystemResult upload(FileBean file) throws IOException;

    /**
     * 根据objectName删除服务器上的文件,objectName指上传时指定的folder+fileName
     *
     * @param bucketName 桶的名称
     * @param objectName folder+fileName 如"test/test.txt"
     */
    void deleteFile(String bucketName, String objectName);

    /**
     * 判断文件是否存在,objectName指上传时指定的folder+fileName
     *
     * @param bucketName 桶的名称
     * @param objectName folder+fileName 如"test/test.txt"
     * @return 是否存在
     */
    boolean isExist(String bucketName, String objectName);

    /**
     * 获取文件URL(私有桶),objectName指上传返回值中的objectName
     *
     * @param bucketName 桶的名称
     * @param objectName 返回值中的objectName
     * @return 返回文件URL
     */
    String getUrl(String bucketName, String objectName);

    /**
     * 获取文件URL(私有桶),objectName指上传返回值中的objectName
     *
     * @param bucketName 桶的名称
     * @param objectName 返回值中的objectName
     * @param expireTime 失效时间，单位（毫秒）
     * @return 返回文件URL
     */
    String getUrl(String bucketName, String objectName, Long expireTime);

    /**
     * 批量获取存储文件的公开URL
     *
     * @param bucketName  桶的名称
     * @param objectNames 返回值中的objectName
     * @return 公开URL  objectName -&gt; Open URL
     */
    Map<String, String> getUrls(String bucketName, List<String> objectNames);

    /**
     * 批量获取存储文件的公开URL
     *
     * @param bucketName  桶的名称
     * @param objectNames 返回值中的objectName
     * @param expireTime  失效时间，单位（毫秒）
     * @return 公开URL  objectName -&gt; Open URL
     */
    Map<String, String> getUrls(String bucketName, List<String> objectNames, Long expireTime);

    /**
     * 流式下载文件,objectName指上传时指定的folder+fileName
     *
     * @param bucketName 桶的名称
     * @param objectName folder+fileName 如"test/test.txt"
     * @param targetDir  要下载到哪个目录下，操作系统文件系统绝对路径
     * @return 下载的文件路径(路径 + 文件名)
     * @throws IOException 创建目录失败
     */
    String downloadFile(String bucketName, String objectName, String targetDir) throws IOException;

    /**
     * 流式下载文件,objectName指上传时指定的folder+fileName
     *
     * @param bucketName 桶的名称
     * @param objectName folder+fileName 如"test/test.txt"
     * @return 文件流
     * @throws IOException 文件流异常
     */
    InputStream downloadStream(String bucketName, String objectName) throws IOException;

    /**
     * 获取文件大小
     *
     * @param bucketName 桶的名称
     * @param objectName folder+fileName 如"test/test.txt"
     * @return 文件大小
     */
    long getFileSize(String bucketName, String objectName);

    /**
     * 生成预签名URL，用于公开访问存储文件
     *
     * @param bucketName   桶的名称
     * @param objectName   返回值中的objectName
     * @param realFileName 实际文件名
     * @return 预签名URL
     */
    String preSignUrl(String bucketName, String objectName, String realFileName);

    /**
     * 生成预签名URL，用于公开访问存储文件
     *
     * @param bucketName   桶的名称
     * @param objectName   返回值中的objectName
     * @param realFileName 实际文件名
     * @param expireTime   失效时间，单位（毫秒）
     * @return 预签名URL
     */
    String preSignUrl(String bucketName, String objectName, String realFileName, Long expireTime);

    /**
     * 获取文件的真实文件名
     *
     * @param bucketName 桶的名称
     * @param objectName folder+fileName 如"test/test.txt"
     * @return 文件的真实文件名
     * @throws FileSystemException 文件系统异常
     */
    String getRealFileName(String bucketName, String objectName) throws FileSystemException;
}
