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
import com.lodsve.boot.filesystem.handler.AliyunOssFileSystemHandler;
import com.lodsve.boot.filesystem.handler.AmazonS3FileSystemHandler;
import com.lodsve.boot.filesystem.handler.FileSystemHandler;
import com.lodsve.boot.filesystem.server.FileSystemServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传组件配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
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
    @ConditionalOnProperty(name = "lodsve.file-system.type", havingValue = "OSS")
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
                properties.getOss().getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret(),
                configuration);
        }

        @Bean
        public FileSystemHandler fileSystemHandler(OSS oss) {
            return new AliyunOssFileSystemHandler(oss, properties.getBucketName());
        }
    }

    @Configuration
    @ConditionalOnClass(AmazonS3.class)
    @ConditionalOnProperty(name = "lodsve.file-system.type", havingValue = "AWS")
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
            return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(properties.getAws().getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(configuration)
                .build();
        }

        @Bean
        @ConditionalOnMissingBean
        public FileSystemHandler fileSystemHandler(AmazonS3 amazonS3) {
            return new AmazonS3FileSystemHandler(amazonS3, properties.getBucketName());
        }
    }
}
