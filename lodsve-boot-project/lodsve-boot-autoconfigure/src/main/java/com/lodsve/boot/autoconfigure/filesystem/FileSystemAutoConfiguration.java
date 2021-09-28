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

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.lodsve.boot.component.filesystem.handler.AliyunOssFileSystemHandler;
import com.lodsve.boot.component.filesystem.handler.AmazonS3FileSystemHandler;
import com.lodsve.boot.component.filesystem.handler.FileSystemHandler;
import com.lodsve.boot.component.filesystem.handler.TencentCosFileSystemHandler;
import com.lodsve.boot.component.filesystem.server.FileSystemServer;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传组件配置.
 *
 * @author Hulk Sun
 */
@Configuration
@ConditionalOnClass(FileSystemServer.class)
@EnableConfigurationProperties(FileSystemProperties.class)
public class FileSystemAutoConfiguration {
    private static FileSystemProperties properties;

    public FileSystemAutoConfiguration(FileSystemProperties properties) {
        FileSystemAutoConfiguration.properties = properties;
    }

    @Bean
    public FileSystemServer fileSystemTemplate(FileSystemHandler handler) {
        return new FileSystemServer(handler);
    }

    @Configuration
    @ConditionalOnClass(OSS.class)
    @ConditionalOnProperty(name = "lodsve.file-system.type", havingValue = "ALIYUN_OSS")
    public static class OssConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ClientBuilderConfiguration clientConfiguration() {
            FileSystemProperties.ClientExtendProperties client = properties.getClient();
            ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
            configuration.setMaxConnections(client.getMaxConnections());
            configuration.setSocketTimeout(client.getSocketTimeout());
            configuration.setConnectionTimeout(client.getConnectionTimeout());
            configuration.setConnectionRequestTimeout(client.getConnectionRequestTimeout());
            client.setIdleConnectionTime(client.getIdleConnectionTime());
            configuration.setMaxErrorRetry(client.getMaxErrorRetry());
            configuration.setSupportCname(client.isSupportCname());
            configuration.setSLDEnabled(client.isSldEnabled());
            if (com.aliyun.oss.common.comm.Protocol.HTTP.toString().equals(client.getProtocol())) {
                configuration.setProtocol(com.aliyun.oss.common.comm.Protocol.HTTP);
            } else if (com.aliyun.oss.common.comm.Protocol.HTTPS.toString().equals(client.getProtocol())) {
                configuration.setProtocol(com.aliyun.oss.common.comm.Protocol.HTTPS);
            }
            configuration.setUserAgent(client.getUserAgent());
            return configuration;
        }

        @Bean
        @ConditionalOnMissingBean
        public OSS ossClient(ClientBuilderConfiguration configuration) {
            return new OSSClientBuilder().build(
                properties.getAliyunOss().getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret(),
                configuration);
        }

        @Bean
        public FileSystemHandler fileSystemHandler(OSS oss) {
            return new AliyunOssFileSystemHandler(oss, properties.getAliyunOss().getEndpoint(), properties.getDefaultExpire(), properties.getBucketAcl());
        }
    }

    @Configuration
    @ConditionalOnClass(AmazonS3.class)
    @ConditionalOnProperty(name = "lodsve.file-system.type", havingValue = "AMAZON_S3")
    public static class AwsConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ClientConfiguration clientConfiguration() {
            FileSystemProperties.ClientExtendProperties client = properties.getClient();

            ClientConfiguration configuration = new ClientConfiguration();
            configuration.setMaxConnections(client.getMaxConnections());
            configuration.setSocketTimeout(client.getSocketTimeout());
            configuration.setConnectionTimeout(client.getConnectionTimeout());
            client.setIdleConnectionTime(client.getIdleConnectionTime());
            configuration.setMaxErrorRetry(client.getMaxErrorRetry());
            if (Protocol.HTTP.toString().equals(client.getProtocol())) {
                configuration.setProtocol(Protocol.HTTP);
            } else if (Protocol.HTTPS.toString().equals(client.getProtocol())) {
                configuration.setProtocol(Protocol.HTTPS);
            }
            return configuration;
        }

        @Bean
        @ConditionalOnMissingBean
        public AmazonS3 amazonS3Client(ClientConfiguration configuration) {
            BasicAWSCredentials credentials = new BasicAWSCredentials(properties.getAccessKeyId(), properties.getAccessKeySecret());
            return AmazonS3ClientBuilder.standard().withRegion(properties.getAwsS3().getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(configuration)
                .build();
        }

        @Bean
        @ConditionalOnMissingBean
        public FileSystemHandler fileSystemHandler(AmazonS3 amazonS3) {
            Regions region = properties.getAwsS3().getRegion();
            String protocol = properties.getClient().getProtocol();
            String endpoint = String.format("%s://s3.%s.amazonaws.com", protocol, StringUtils.lowerCase(region.getName()));
            return new AmazonS3FileSystemHandler(amazonS3, endpoint, properties.getDefaultExpire(), properties.getBucketAcl());
        }
    }

    @Configuration
    @ConditionalOnClass(COSClient.class)
    @ConditionalOnProperty(name = "lodsve.file-system.type", havingValue = "TENCENT_COS")
    public static class TencentCosConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ClientConfig clientConfiguration() {
            FileSystemProperties.ClientExtendProperties client = properties.getClient();

            ClientConfig configuration = new ClientConfig();
            configuration.setMaxConnectionsCount(client.getMaxConnections());
            configuration.setSocketTimeout(client.getSocketTimeout());
            configuration.setConnectionTimeout(client.getConnectionTimeout());
            client.setIdleConnectionTime(client.getIdleConnectionTime());
            configuration.setMaxErrorRetry(client.getMaxErrorRetry());
            if (Protocol.HTTP.toString().equals(client.getProtocol())) {
                configuration.setHttpProtocol(HttpProtocol.http);
            } else if (Protocol.HTTPS.toString().equals(client.getProtocol())) {
                configuration.setHttpProtocol(HttpProtocol.https);
            }
            return configuration;
        }

        @Bean
        @ConditionalOnMissingBean
        public COSClient tencentCosClient(ClientConfig configuration) {
            // 1 初始化用户身份信息（secretId, secretKey）。
            COSCredentials cred = new BasicCOSCredentials(properties.getAccessKeyId(), properties.getAccessKeySecret());
            // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
            // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
            Region region = new Region(properties.getTencentCos().getRegion());
            configuration.setRegion(region);
            // 3 生成 cos 客户端。
            return new COSClient(cred, configuration);
        }

        @Bean
        @ConditionalOnMissingBean
        public FileSystemHandler fileSystemHandler(COSClient cosClient) {
            return new TencentCosFileSystemHandler(cosClient, properties.getClient().getProtocol(), properties.getDefaultExpire(), properties.getBucketAcl());
        }
    }
}
