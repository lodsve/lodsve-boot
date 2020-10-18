package com.lodsve.boot.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactorySupport;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.function.BiFunction;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class DynamicMongoClientFactory extends MongoClientFactorySupport<MongoClient> {
    protected DynamicMongoClientFactory(MongoProperties properties,
                                        Environment environment,
                                        List<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                                        BiFunction<MongoClientSettings, MongoDriverInformation, MongoClient> clientCreator) {
        super(properties, environment, builderCustomizers, clientCreator);
    }

    public DynamicMongoClientFactory(MongoProperties properties,
                                     Environment environment, List<MongoClientSettingsBuilderCustomizer> customizers) {
        super(properties, environment, customizers, (settings, mongoDriverInformation) -> {
            MongoDriverInformation.Builder builder = mongoDriverInformation == null ? MongoDriverInformation.builder()
                : MongoDriverInformation.builder(mongoDriverInformation);
            return new DynamicMongoClient(settings, builder.driverName("sync").build());
        });
    }
}
