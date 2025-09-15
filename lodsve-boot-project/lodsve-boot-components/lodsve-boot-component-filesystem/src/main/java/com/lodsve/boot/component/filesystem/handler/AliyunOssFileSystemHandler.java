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

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.lodsve.boot.component.filesystem.bean.FileBean;
import com.lodsve.boot.component.filesystem.bean.FileSystemResult;
import com.lodsve.boot.component.filesystem.enums.AccessControlEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.util.Date;
import java.util.Map;

/**
 * 阿里云oss上传.
 *
 * @author Hulk Sun
 */
public class AliyunOssFileSystemHandler extends AbstractFileSystemHandler {
    private static final Logger logger = LoggerFactory.getLogger(AliyunOssFileSystemHandler.class);
    private final OSS client;

    public AliyunOssFileSystemHandler(OSS client, String endpoint, Long defaultExpire, Map<String, Boolean> bucketAcl) {
        super(endpoint, defaultExpire, bucketAcl);
        this.client = client;
    }

    @Override
    public boolean createFolder(String bucketName, String folder) {
        // 判断文件夹是否存在，不存在则创建
        if (client.doesObjectExist(bucketName, folder)) {
            return true;
        }

        // 创建文件夹
        PutObjectResult result = client.putObject(bucketName, folder, new ByteArrayInputStream(new byte[0]));
        return null != result && StringUtils.isNotBlank(result.getETag());
    }

    @Override
    public FileSystemResult upload(FileBean file) throws IOException {
        if (StringUtils.isBlank(file.getBucketName())) {
            throw new FileSystemException("bucket name can't be null!");
        }

        FileSystemResult result = new FileSystemResult();
        String fileName = file.getFileName();

        // 以输入流的形式上传文件
        InputStream content = file.getContent();
        // 创建上传Object的MetadataBinaryUtil
        ObjectMetadata metadata = new ObjectMetadata();
        if (file.getValidatorMd5()) {
            String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(IOUtils.toByteArray(content)));
            metadata.setContentMD5(md5);
            result.setMd5(md5);
        }

        // 上传的文件的长度
        metadata.setContentLength(content.available());
        // 指定该Object被下载时的网页的缓存行为
        metadata.setCacheControl("no-cache");
        // 指定该Object下设置Header
        metadata.setHeader("Pragma", "no-cache");
        // 指定该Object被下载时的内容编码格式
        metadata.setContentEncoding("utf-8");
        // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
        // 如果没有扩展名则填默认值application/octet-stream
        metadata.setContentType(file.getContentType());
        // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
        metadata.setContentDisposition("attachment;filename=\"" + fileName + "\"");
        // 设置访问权限
        metadata.setObjectAcl(evalAccessControlValue(file.getAccessControl()));
        try {
            // 上传文件 (上传文件流的形式)
            PutObjectResult putResult = client.putObject(file.getBucketName(), file.getFinalFileName(), content, metadata);

            // 解析结果
            result.setObjectName(file.getFinalFileName());
            result.setEtag(putResult.getETag());
            result.setFileName(fileName);
            result.setResult(StringUtils.isNotBlank(putResult.getETag()));
            return result;
        } catch (OSSException | ClientException e) {
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteFile(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        client.deleteObject(bucketName, objectName);
        logger.info("删除" + bucketName + "下的文件" + objectName + "成功");
    }

    @Override
    public boolean isExist(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        boolean exist = client.doesObjectExist(bucketName, objectName);
        logger.info(bucketName + "下的文件" + objectName + "存在：" + exist);
        return exist;
    }

    @Override
    public String getUrl(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        return getUrl(bucketName, objectName, defaultExpire);
    }

    @Override
    public String getUrl(String bucketName, String objectName, Long expireTime) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        // 判断桶的权限
        boolean isPublic = isPublic(bucketName);
        if (isPublic) {
            return getOpenUrl(bucketName, objectName);
        } else {
            return getPrivateUrl(bucketName, objectName, expireTime);
        }
    }

    /**
     * 获取公开地址
     *
     * @param bucketName 桶的名称
     * @param objectName 对象名
     * @return 公开地址
     */
    private String getOpenUrl(String bucketName, String objectName) {
        // http://${bucketName}.${endpointUpload}/${objectName}
        String[] temp = StringUtils.split(endpoint, "://");
        return String.format("%s://%s.%s/%s", temp[0], bucketName, temp[1], objectName);

    }

    /**
     * 获取私有地址
     *
     * @param bucketName 桶的名称
     * @param objectName 返回值中的objectName
     * @param expireTime 失效时间，单位（毫秒）
     * @return 私有地址
     */
    private String getPrivateUrl(String bucketName, String objectName, Long expireTime) {
        // 设置URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + expireTime);
        // 生成URL
        URL url = client.generatePresignedUrl(bucketName, objectName, expiration);
        return url.toString();
    }

    @Override
    public void download(String bucketName, String objectName, File downloadFile) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        client.getObject(new GetObjectRequest(bucketName, objectName), downloadFile);
    }

    @Override
    public String resolveRealName(String bucketName, String objectName) throws FileSystemException {
        Assert.hasText(bucketName, "bucket name can't be null!");

        ObjectMetadata metadata = client.getObjectMetadata(bucketName, objectName);
        if (null == metadata) {
            throw new FileSystemException(String.format("Object Metadata is NULL! Object name is [%s]!", objectName));
        }

        String disposition = metadata.getContentDisposition();
        String realName = StringUtils.removeStart(disposition, "attachment;filename=\"");
        realName = StringUtils.removeEnd(realName, "\"");
        return realName;
    }

    @Override
    public void destroy() throws Exception {
        if (null != client) {
            client.shutdown();
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
                return CannedAccessControlList.Default;
        }
    }
}
