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
import com.amazonaws.regions.Regions;
import com.lodsve.boot.component.filesystem.enums.FileSystemTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * 文件上传组件配置.
 *
 * @author Hulk Sun
 */
@ConfigurationProperties(prefix = "lodsve.file-system")
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

    public FileSystemTypeEnum getType() {
        return type;
    }

    public void setType(FileSystemTypeEnum type) {
        this.type = type;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public Long getDefaultExpire() {
        return defaultExpire;
    }

    public void setDefaultExpire(Long defaultExpire) {
        this.defaultExpire = defaultExpire;
    }

    public Map<String, Boolean> getBucketAcl() {
        return bucketAcl;
    }

    public void setBucketAcl(Map<String, Boolean> bucketAcl) {
        this.bucketAcl = bucketAcl;
    }

    public AliyunOssProperties getAliyunOss() {
        return aliyunOss;
    }

    public void setAliyunOss(AliyunOssProperties aliyunOss) {
        this.aliyunOss = aliyunOss;
    }

    public AwsS3Properties getAwsS3() {
        return awsS3;
    }

    public void setAwsS3(AwsS3Properties awsS3) {
        this.awsS3 = awsS3;
    }

    public TencentCosProperties getTencentCos() {
        return tencentCos;
    }

    public void setTencentCos(TencentCosProperties tencentCos) {
        this.tencentCos = tencentCos;
    }

    public ClientExtendProperties getClient() {
        return client;
    }

    public void setClient(ClientExtendProperties client) {
        this.client = client;
    }

    /**
     * AWS基本服務端屬性.
     */
    public static class AwsS3Properties {
        /**
         * 区域
         */
        private Regions region = Regions.DEFAULT_REGION;

        public Regions getRegion() {
            return region;
        }

        public void setRegion(Regions region) {
            this.region = region;
        }
    }

    /**
     * OSS基本服務端屬性.
     */
    public static class AliyunOssProperties {
        /**
         * 上传目标地址
         */
        private String endpoint;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

    /**
     * COS基本服務端屬性.
     */
    public static class TencentCosProperties {
        /**
         * 区域
         */
        private String region;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }

    /**
     * 客户端扩展配置
     */
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

        public int getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public int getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }

        public long getIdleConnectionTime() {
            return idleConnectionTime;
        }

        public void setIdleConnectionTime(long idleConnectionTime) {
            this.idleConnectionTime = idleConnectionTime;
        }

        public int getMaxErrorRetry() {
            return maxErrorRetry;
        }

        public void setMaxErrorRetry(int maxErrorRetry) {
            this.maxErrorRetry = maxErrorRetry;
        }

        public boolean isSupportCname() {
            return supportCname;
        }

        public void setSupportCname(boolean supportCname) {
            this.supportCname = supportCname;
        }

        public boolean isSldEnabled() {
            return sldEnabled;
        }

        public void setSldEnabled(boolean sldEnabled) {
            this.sldEnabled = sldEnabled;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }
    }

}
