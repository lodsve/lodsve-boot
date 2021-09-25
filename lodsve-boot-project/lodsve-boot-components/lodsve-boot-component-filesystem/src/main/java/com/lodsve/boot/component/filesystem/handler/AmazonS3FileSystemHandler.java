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

import com.aliyun.oss.common.utils.BinaryUtil;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.SdkHttpUtils;
import com.lodsve.boot.component.filesystem.bean.FileBean;
import com.lodsve.boot.component.filesystem.bean.Result;
import com.lodsve.boot.component.filesystem.enums.AccessControlEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.util.Date;

/**
 * 亚马逊云s3.
 *
 * @author Hulk Sun
 */
public class AmazonS3FileSystemHandler extends AbstractFileSystemHandler {
    private static final Logger logger = LoggerFactory.getLogger(AmazonS3FileSystemHandler.class);
    private final String bucketName;
    private final AmazonS3 amazonS3Client;

    public AmazonS3FileSystemHandler(AmazonS3 amazonS3Client, String bucketName) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
    }

    @Override
    public void download(String objectName, File downloadFile) {

    }

    @Override
    public String resolveRealName(String objectName) throws FileSystemException {
        return null;
    }

    @Override
    public Result upload(FileBean file) throws IOException {
        Result result = new Result();

        // 以输入流的形式上传文件
        InputStream content = file.getContent();
        // 文件名
        String fileName = file.getFileName();

        // 创建上传Object
        ObjectMetadata objectMetadata = new ObjectMetadata();
        if (file.getValidatorMd5()) {
            String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(IOUtils.toByteArray(content)));
            objectMetadata.setContentMD5(md5);
            result.setMd5(md5);
        }

        // 指定该Object被下载时的网页的缓存行为
        objectMetadata.setCacheControl("no-cache");
        // 上传的文件的长度
        objectMetadata.setContentLength(content.available());
        // 指定该Object下设置Header
        objectMetadata.setHeader("Pragma", "no-cache");
        // 指定该Object被下载时的内容编码格式
        objectMetadata.setContentEncoding("utf-8");
        // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
        // 如果没有扩展名则填默认值application/octet-stream
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentDisposition("attachment;filename=\"" + SdkHttpUtils.urlEncode(fileName, false) + "\"");
        try {
            // 上传文件 (上传文件流的形式) 并设置未公开
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getFinalFileName(), content, objectMetadata)
                .withCannedAcl(evalAccessControlValue(file.getAccessControl()));
            PutObjectResult putResult = amazonS3Client.putObject(putObjectRequest);

            // 解析结果
            result.setObjectName(file.getFinalFileName());
            result.setEtag(putResult.getETag());
            result.setFileName(fileName);
            result.setResult(StringUtils.isNotBlank(putResult.getETag()));
            return result;
        } catch (SdkClientException e) {
            logger.error("上传亚马逊云S3服务器异常." + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteFile(String objectName) {
        amazonS3Client.deleteObject(bucketName, objectName);
        logger.info("删除" + bucketName + "下的文件" + objectName + "成功");
    }

    @Override
    public boolean isExist(String objectName) {
        boolean exist = amazonS3Client.doesObjectExist(bucketName, objectName);
        logger.info(bucketName + "下的文件" + objectName + "存在：" + exist);
        return exist;
    }

    @Override
    public String getUrl(String objectName) {
        URL url = amazonS3Client.getUrl(bucketName, objectName);
        return url.toString();
    }

    @Override
    public String getUrl(String objectName, Long expireTime) {
        // 设置URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + expireTime);
        URL url = amazonS3Client.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }

    @Override
    public void destroy() throws Exception {
        if (amazonS3Client != null) {
            amazonS3Client.shutdown();
        }
    }

    private CannedAccessControlList evalAccessControlValue(AccessControlEnum accessControlEnum) {
        accessControlEnum = (null == accessControlEnum ? AccessControlEnum.DEFAULT : accessControlEnum);
        switch (accessControlEnum) {
            case PRIVATE:
                return CannedAccessControlList.Private;
            case PUBLIC_READ:
                return CannedAccessControlList.PublicRead;
            case PUBLIC_READ_WRITE:
                return CannedAccessControlList.PublicReadWrite;
            case DEFAULT:
            default:
                return CannedAccessControlList.AuthenticatedRead;
        }
    }
}
