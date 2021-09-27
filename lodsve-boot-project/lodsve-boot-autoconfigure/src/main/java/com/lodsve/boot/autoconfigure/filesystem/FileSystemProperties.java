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
package com.lodsve.boot.autoconfigure.filesystem;

import com.aliyun.oss.common.comm.Protocol;
import com.lodsve.boot.component.filesystem.enums.FileSystemTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * 文件上传组件配置.
 *
 * @author Hulk Sun
 */
@ConfigurationProperties(prefix = "lodsve.file-system")
@Data
public class FileSystemProperties {
    /**
     * 使用的文件上传类型
     */
    private FileSystemTypeEnum type = FileSystemTypeEnum.ALIYUN_OSS;
    /**
     * accessKeyId
     */
    private String accessKeyId;
    /**
     * accessKeySecret
     */
    private String accessKeySecret;
    /**
     * URL默认失效时间，单位是毫秒，默认10分钟
     */
    private Long defaultExpire = 10 * 60 * 1000L;
    /**
     * 存储桶是否是公开的
     * bucketName -&gt; true/false
     */
    @NestedConfigurationProperty
    private Map<String, Boolean> bucketAcl;
    /**
     * 阿里云oss配置
     */
    @NestedConfigurationProperty
    private AliyunOssProperties aliyunOss = new AliyunOssProperties();
    /**
     * 亚马逊云s3配置
     */
    @NestedConfigurationProperty
    private AwsS3Properties awsS3 = new AwsS3Properties();
    /**
     * 腾讯云COS配置
     */
    @NestedConfigurationProperty
    private TencentCosProperties tencentCos = new TencentCosProperties();
    /**
     * 客户端扩展配置
     */
    @NestedConfigurationProperty
    private ClientExtendProperties client = new ClientExtendProperties();

    /**
     * AWS基本服務端屬性.
     */
    @Data
    public static class AwsS3Properties {
        /**
         * 区域
         */
        private String region;
    }

    /**
     * OSS基本服務端屬性.
     */
    @Data
    public static class AliyunOssProperties {
        /**
         * 上传目标地址
         */
        private String endpoint;
    }

    /**
     * COS基本服務端屬性.
     */
    @Data
    public static class TencentCosProperties {
        /**
         * 区域
         */
        private String region;
    }

    /**
     * 客户端扩展配置
     */
    @Data
    public static class ClientExtendProperties {
        /**
         * 允许打开的最大HTTP连接数。默认为1024
         */
        private int maxConnections = 1024;
        /**
         * Socket层传输数据的超时时间（单位：毫秒）。默认为50000毫秒
         */
        private int socketTimeout = 50000;
        /**
         * 建立连接的超时时间（单位：毫秒）。默认为50000毫秒
         */
        private int connectionTimeout = 50000;
        /**
         * 从连接池中获取连接的超时时间（单位：毫秒）。默认不超时
         */
        private int connectionRequestTimeout;
        /**
         * 如果空闲时间超过此参数的设定值，则关闭连接（单位：毫秒）。默认为60000毫秒
         */
        private long idleConnectionTime = 60000;
        /**
         * 请求失败后最大的重试次数。默认3次
         */
        private int maxErrorRetry = 3;
        /**
         * 是否支持CNAME作为Endpoint，默认支持CNAME
         */
        private boolean supportCname = true;
        /**
         * 是否开启二级域名（Second Level Domain）的访问方式，默认不开启
         */
        private boolean sldEnabled = false;
        /**
         * 连接OSS所采用的协议（HTTP/HTTPS），默认为HTTP
         */
        private String protocol = Protocol.HTTP.toString();
        /**
         * 用户代理，指HTTP的User-Agent头。默认为”aliyun-sdk-java”
         */
        private String userAgent = "aliyun-sdk-java";
    }

}
