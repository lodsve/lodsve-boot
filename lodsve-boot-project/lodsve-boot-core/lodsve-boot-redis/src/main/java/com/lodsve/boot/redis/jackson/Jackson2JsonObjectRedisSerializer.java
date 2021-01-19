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
package com.lodsve.boot.redis.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

/**
 * 重写jackson存储redis数据的序列化和反序列化.<p/>
 * 主要是为了保留原始对象的类型
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @see Jackson2JsonRedisSerializer
 */
public class Jackson2JsonObjectRedisSerializer implements RedisSerializer<Object> {
    protected GenericJackson2JsonRedisSerializer serializer;

    public Jackson2JsonObjectRedisSerializer() {
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());
        om.registerModule((new SimpleModule()).addSerializer(new NullValueSerializer()));
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        this.serializer = new GenericJackson2JsonRedisSerializer(om);
    }

    public Jackson2JsonObjectRedisSerializer(ObjectMapper om) {
        this.serializer = new GenericJackson2JsonRedisSerializer(om);
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
        private static final long serialVersionUID = 1999052150548658807L;
        private final String classIdentifier = "@class";

        NullValueSerializer() {
            super(NullValue.class);
        }

        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            jgen.writeStringField(this.classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }
}
