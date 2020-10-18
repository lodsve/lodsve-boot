package com.lodsve.boot.autoconfigure.mongo;

import com.lodsve.boot.mongodb.DynamicMongoClient;
import com.lodsve.boot.mongodb.DynamicMongoClientFactory;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.stream.Collectors;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({DynamicMongoClient.class, MongoClient.class})
@EnableConfigurationProperties(DynamicMongoProperties.class)
@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDatabaseFactory")
public class DynamicMongoAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(MongoClient.class)
    public MongoClient mongo(DynamicMongoProperties properties, Environment environment,
                             ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                             ObjectProvider<MongoClientSettings> settings) {
        return new DynamicMongoClientFactory(
            null,
            environment,
            builderCustomizers.orderedStream().collect(Collectors.toList())).createMongoClient(settings.getIfAvailable()
        );
    }
}
