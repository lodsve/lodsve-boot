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
package com.lodsve.boot.json.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.lodsve.boot.bean.Codeable;
import com.lodsve.boot.utils.EnumUtils;

import java.io.IOException;

/**
 * gson对枚举类型的序列化、反序列化定制.
 *
 * @author sunhao(hulk)
 */
public class CodeableTypeAdapterFactory implements TypeAdapterFactory {
    private final Class<? extends Codeable> clazz;

    public CodeableTypeAdapterFactory(Class<? extends Codeable> clazz) {
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (Codeable.class.isAssignableFrom(rawType) && clazz.equals(rawType)) {
            return (TypeAdapter<T>) new CodeableTypeAdapter(clazz);
        }
        return null;
    }

    public static class CodeableTypeAdapter extends TypeAdapter<Codeable> {
        private final Class<? extends Codeable> clazz;

        public CodeableTypeAdapter(Class<? extends Codeable> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void write(JsonWriter out, Codeable value) throws IOException {
            if (null == value) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("code");
            out.value(value.getCode());
            out.name("title");
            out.value(value.getTitle());
            out.endObject();
        }

        @Override
        public Codeable read(JsonReader in) throws IOException {
            return EnumUtils.evalByValue(clazz, in.nextString());
        }
    }
}
