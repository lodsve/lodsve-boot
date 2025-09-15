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
package com.lodsve.boot.component.filesystem.bean;

import com.lodsve.boot.component.filesystem.enums.AccessControlEnum;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 上传文件.
 *
 * @author Hulk Sun
 */
public class FileBean implements Serializable {
    /**
     * 需要上传到哪个桶
     */
    private String bucketName;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件流
     */
    private InputStream content;
    /**
     * 文件大小
     */
    private Long fileSize;
    /**
     * 是否要检验文件md5值，防止上传过程中发生了异常
     */
    private Boolean validatorMd5;
    /**
     * 文件内容类型 content-type
     */
    private String contentType;
    /**
     * 上传的目录
     */
    private String folder;
    /**
     * 最终要上传的文件名
     */
    private String finalFileName;
    /**
     * 文件访问权限，详见{@link AccessControlEnum}
     */
    private AccessControlEnum accessControl;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Boolean getValidatorMd5() {
        return validatorMd5;
    }

    public void setValidatorMd5(Boolean validatorMd5) {
        this.validatorMd5 = validatorMd5;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFinalFileName() {
        return finalFileName;
    }

    public void setFinalFileName(String finalFileName) {
        this.finalFileName = finalFileName;
    }

    public AccessControlEnum getAccessControl() {
        return accessControl;
    }

    public void setAccessControl(AccessControlEnum accessControl) {
        this.accessControl = accessControl;
    }

    @Override
    public String toString() {
        final StringBuilder json = new StringBuilder("{");
        json.append("\"bucketName\":\"")
            .append(bucketName).append('\"');

        json.append(",\"fileName\":\"")
            .append(fileName).append('\"');

        json.append(",\"content\":")
            .append(content);

        json.append(",\"fileSize\":")
            .append(fileSize);

        json.append(",\"validatorMd5\":")
            .append(validatorMd5);

        json.append(",\"contentType\":\"")
            .append(contentType).append('\"');

        json.append(",\"folder\":\"")
            .append(folder).append('\"');

        json.append(",\"finalFileName\":\"")
            .append(finalFileName).append('\"');

        json.append(",\"accessControl\":")
            .append(accessControl);

        json.append('}');
        return json.toString();
    }
}
