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
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.util.SdkHttpUtils;
import com.lodsve.boot.component.filesystem.bean.FileBean;
import com.lodsve.boot.component.filesystem.bean.FileSystemResult;
import com.lodsve.boot.component.filesystem.enums.AccessControlEnum;
import com.lodsve.boot.utils.UrlEncoderUtil;
import org.apache.commons.io.FilenameUtils;
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
 * 亚马逊云s3.
 *
 * @author Hulk Sun
 */
public class AmazonS3FileSystemHandler extends AbstractFileSystemHandler {
    private static final Logger logger = LoggerFactory.getLogger(AmazonS3FileSystemHandler.class);
    private final AmazonS3 amazonS3Client;

    public AmazonS3FileSystemHandler(AmazonS3 amazonS3Client, String endpoint, Long defaultExpire, Map<String, Boolean> bucketAcl) {
        super(endpoint, defaultExpire, bucketAcl);
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public void download(String bucketName, String objectName, File downloadFile) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        amazonS3Client.getObject(new GetObjectRequest(bucketName, objectName), downloadFile);
    }

    @Override
    public InputStream download(String bucketName, String objectName) throws FileSystemException {
        Assert.hasText(bucketName, "bucket name can't be null!");
        try (InputStream in = amazonS3Client.getObject(new GetObjectRequest(bucketName, objectName)).getObjectContent()) {
            return in;
        } catch (Exception e) {
            logger.error("download amazon s3 object error: {}", e.getMessage(), e);
            throw new FileSystemException(e.getMessage());
        }
    }

    @Override
    public String getRealFileName(String bucketName, String objectName) throws FileSystemException {
        Assert.hasText(bucketName, "bucket name can't be null!");

        ObjectMetadata metadata = amazonS3Client.getObjectMetadata(bucketName, objectName);
        if (null == metadata) {
            throw new FileSystemException(String.format("Object Metadata is NULL! Object name is [%s]!", objectName));
        }

        String disposition = metadata.getContentDisposition();
        return StringUtils.removeEnd(StringUtils.removeStart(disposition, "attachment;filename=\""), "\"");
    }

    @Override
    public boolean createFolder(String bucketName, String folder) {
        // 判断文件夹是否存在，不存在则创建
        if (amazonS3Client.doesObjectExist(bucketName, folder)) {
            return true;
        }

        // 创建文件夹
        // 创建文件夹
        PutObjectRequest request = new PutObjectRequest(bucketName, folder, new ByteArrayInputStream(new byte[0]),
            null).withCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = amazonS3Client.putObject(request);
        return null != result && StringUtils.isNotBlank(result.getETag());
    }

    @Override
    public FileSystemResult upload(FileBean file) throws IOException {
        if (StringUtils.isBlank(file.getBucketName())) {
            throw new FileSystemException("bucket name can't be null!");
        }

        FileSystemResult result = new FileSystemResult();

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
            PutObjectRequest putObjectRequest = new PutObjectRequest(file.getBucketName(), file.getFinalFileName(), content, objectMetadata)
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
    public void deleteFile(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        amazonS3Client.deleteObject(bucketName, objectName);
        logger.info("删除{}下的文件{}成功", bucketName, objectName);
    }

    @Override
    public boolean isExist(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        boolean exist = amazonS3Client.doesObjectExist(bucketName, objectName);
        logger.info("{}下的文件{}存在：{}", bucketName, objectName, exist);
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

    @Override
    public long getFileSize(String bucketName, String objectName) {
        Assert.hasText(bucketName, "bucket name can't be null!");

        try {
            ObjectMetadata metadata = amazonS3Client.getObjectMetadata(bucketName, objectName);
            return metadata.getContentLength();
        } catch (Exception e) {
            logger.error("stat aliyun oss object error: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public String preSignUrl(String bucketName, String objectName, String realFileName) {
        Assert.hasText(bucketName, "bucket name can't be null!");
        Assert.hasText(objectName, "object name can't be null!");

        return preSignUrl(bucketName, objectName, realFileName, defaultExpire);
    }

    @Override
    public String preSignUrl(String bucketName, String objectName, String realFileName, Long expireTime) {
        Assert.hasText(bucketName, "bucket name can't be null!");
        Assert.hasText(objectName, "object name can't be null!");

        return generateUrlByMethod(bucketName, objectName, expireTime, realFileName, HttpMethod.PUT);
    }

    /**
     * 获取公开地址
     *
     * @param bucketName 桶的名称
     * @param objectName 对象名
     * @return 公开地址
     */
    protected String getOpenUrl(String bucketName, String objectName) {
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
        Assert.hasText(bucketName, "bucket name can't be null!");
        Assert.hasText(objectName, "object name can't be null!");
        // 先获取原始文件名称
        String realFileName;
        try {
            realFileName = getRealFileName(bucketName, objectName);
        } catch (FileSystemException e) {
            realFileName = FilenameUtils.getName(objectName);
        }


        return generateUrlByMethod(bucketName, objectName, expireTime, realFileName, HttpMethod.GET);
    }

    @Override
    public void destroy() throws Exception {
        if (amazonS3Client != null) {
            amazonS3Client.shutdown();
        }
    }

    private CannedAccessControlList evalAccessControlValue(AccessControlEnum accessControlEnum) {
        accessControlEnum = (null == accessControlEnum ? AccessControlEnum.DEFAULT : accessControlEnum);
        return switch (accessControlEnum) {
            case PRIVATE -> CannedAccessControlList.Private;
            case PUBLIC_READ -> CannedAccessControlList.PublicRead;
            case PUBLIC_READ_WRITE -> CannedAccessControlList.PublicReadWrite;
            default -> CannedAccessControlList.AuthenticatedRead;
        };
    }

    private String generateUrlByMethod(String bucketName, String objectName, Long expireTime, String realFileName, HttpMethod method) {
        // 设置URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + expireTime);
        String contentDisposition = String.format("attachment; filename=\"%s\"", realFileName);
        contentDisposition = UrlEncoderUtil.encodeUriComponent(contentDisposition);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName, method);
        request.setExpiration(expiration);
        request.putCustomRequestHeader("Content-Disposition", contentDisposition);

        // 生成URL
        URL url = amazonS3Client.generatePresignedUrl(request);
        return url.toString();
    }
}
