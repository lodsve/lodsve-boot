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
