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
package com.lodsve.boot.component.redis.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

/**
 * 重写jackson存储redis数据的序列化和反序列化.
 * 主要是为了保留原始对象的类型
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @see Jackson2JsonRedisSerializer
 */
public class Jackson2JsonObjectRedisSerializer implements RedisSerializer<Object> {
    protected GenericJackson2JsonRedisSerializer serializer;

    public Jackson2JsonObjectRedisSerializer(ObjectMapper objectMapper) {
        ObjectMapper objectMapperForRedis = new ObjectMapper();
        if (null != objectMapper) {
            BeanUtils.copyProperties(objectMapper, objectMapperForRedis);
        }

        objectMapperForRedis.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapperForRedis.registerModule(new JavaTimeModule());
        objectMapperForRedis.registerModule((new SimpleModule()).addSerializer(new NullValueSerializer()));
        objectMapperForRedis.activateDefaultTyping(objectMapperForRedis.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        this.serializer = new GenericJackson2JsonRedisSerializer(objectMapperForRedis);
    }

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return serializer.serialize(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return serializer.deserialize(bytes);
    }

    protected static class NullValueSerializer extends StdSerializer<NullValue> {
        NullValueSerializer() {
            super(NullValue.class);
        }

        @Override
        public void serialize(NullValue value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeStartObject();
            String classIdentifier = "@class";
            generator.writeStringField(classIdentifier, NullValue.class.getName());
            generator.writeEndObject();
        }
    }
}
