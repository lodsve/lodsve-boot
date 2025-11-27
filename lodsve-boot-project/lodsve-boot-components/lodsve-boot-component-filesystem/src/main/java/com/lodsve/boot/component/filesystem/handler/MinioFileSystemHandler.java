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
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;

/**
 * Minio 存储实现.
 *
 * @author Hulk Sun
 */
public class MinioFileSystemHandler extends AbstractFileSystemHandler {
    private static final Logger logger = LoggerFactory.getLogger(MinioFileSystemHandler.class);
    private final MinioClient client;

    public MinioFileSystemHandler(MinioClient client, String endpoint, Long defaultExpire, Map<String, Boolean> bucketAcl) {
        super(endpoint, defaultExpire, bucketAcl);
        this.client = client;
    }

    @Override
    public boolean createFolder(String bucketName, String folder) {
        try {
            if (isExist(bucketName, folder)) {
                return true;
            }

            ByteArrayInputStream empty = new ByteArrayInputStream(new byte[0]);
            PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(folder)
                .stream(empty, 0, -1)
                .contentType("application/octet-stream")
                .build();
            client.putObject(args);
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("create folder error: " + e.getMessage(), e);
            }
            return false;
        }
    }

    @Override
    public FileSystemResult upload(FileBean file) throws IOException {
        if (StringUtils.isBlank(file.getBucketName())) {
            throw new FileSystemException("bucket name can't be null!");
        }

        FileSystemResult result = new FileSystemResult();
        String fileName = file.getFileName();

        InputStream content = file.getContent();
        Map<String, String> headers = new HashMap<>(4);
        headers.put("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        String contentType = file.getContentType();
        long size = content.available();
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                .bucket(file.getBucketName())
                .object(file.getFinalFileName())
                .stream(content, size, -1)
                .contentType(contentType)
                .headers(headers)
                .build();
            io.minio.ObjectWriteResponse writeResponse = client.putObject(args);

            result.setObjectName(file.getFinalFileName());
            result.setEtag(writeResponse.etag());
            result.setFileName(fileName);
            result.setResult(StringUtils.isNotBlank(writeResponse.etag()));
            return result;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("upload to minio error: " + e.getMessage(), e);
            }
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        try {
            RemoveObjectArgs args = RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build();
            client.removeObject(args);
            if (logger.isInfoEnabled()) {
                logger.info("删除" + bucketName + "下的文件" + objectName + "成功");
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("delete minio object error: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean isExist(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        try {
            StatObjectResponse stat = client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            boolean exist = stat != null;
            if (logger.isInfoEnabled()) {
                logger.info(bucketName + "下的文件" + objectName + "存在：" + exist);
            }
            return exist;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("stat minio object error: " + e.getMessage(), e);
            }
            return false;
        }
    }

    @Override
    public String getUrl(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");
        return getUrl(bucketName, objectName, defaultExpire);
    }

    @Override
    public String getUrl(String bucketName, String objectName, Long expireTime) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        boolean isPublic = isPublic(bucketName);
        if (isPublic) {
            return getOpenUrl(bucketName, objectName);
        } else {
            return getPrivateUrl(bucketName, objectName, expireTime);
        }
    }

    private String getOpenUrl(String bucketName, String objectName) {
        String base = endpoint;
        String e = StringUtils.removeEnd(base, "/");
        return String.format("%s/%s/%s", e, bucketName, objectName);
    }

    private String getPrivateUrl(String bucketName, String objectName, Long expireTime) {
        int seconds = (int) Math.max(1, Math.min(604800, expireTime / 1000));
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(seconds)
                .build();
            return client.getPresignedObjectUrl(args);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("generate minio presigned url error: " + e.getMessage(), e);
            }
            return null;
        }
    }

    @Override
    public void download(String bucketName, String objectName, File downloadFile) {
        Assert.hasText(bucketName, "bucket name can't be null!");
        try (InputStream in = client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
             FileOutputStream out = new FileOutputStream(downloadFile)) {
            IOUtils.copy(in, out);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("download minio object error: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public String resolveRealName(String bucketName, String objectName) throws FileSystemException {
        Assert.hasText(bucketName, "bucket name can't be null!");

        try {
            StatObjectResponse metadata = client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            if (null == metadata) {
                throw new FileSystemException(String.format("Object Metadata is NULL! Object name is [%s]!", objectName));
            }
            String disposition = metadata.headers().get("Content-Disposition");
            String realName = StringUtils.removeStart(disposition, "attachment;filename=\"");
            realName = StringUtils.removeEnd(realName, "\"");
            return realName;
        } catch (ErrorResponseException e) {
            throw new FileSystemException(e.getMessage());
        } catch (Exception e) {
            throw new FileSystemException(e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }
}
