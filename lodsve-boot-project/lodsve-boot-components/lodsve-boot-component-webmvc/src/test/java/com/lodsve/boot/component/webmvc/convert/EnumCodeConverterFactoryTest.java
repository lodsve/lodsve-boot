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
package com.lodsve.boot.component.webmvc.convert;

import com.lodsve.boot.bean.Codeable;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

public class EnumCodeConverterFactoryTest {
    private final EnumCodeConverterFactory factory = new EnumCodeConverterFactory();
    private final Converter<String, Status> converter = factory.getConverter(Status.class);

    @Test
    public void convertsNamesBeforeCodes() {
        Assert.assertSame(Status.ACTIVE, converter.convert("ACTIVE"));
        Assert.assertSame(Status.INACTIVE, converter.convert("INACTIVE"));
        Assert.assertSame(Status.ACTIVE, converter.convert("1"));
    }

    @Test
    public void returnsNullForBlankAndUnknownValues() {
        Assert.assertNull(converter.convert(null));
        Assert.assertNull(converter.convert(""));
        Assert.assertNull(converter.convert("   "));
        Assert.assertNull(converter.convert("unknown"));
        Assert.assertNull(converter.convert("active"));
    }

    @Test
    public void matchesOnlyCodeableEnums() {
        Assert.assertTrue(factory.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Status.class)));
        Assert.assertFalse(factory.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Thread.State.class)));
        Assert.assertFalse(factory.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(String.class)));
    }

    private enum Status implements Codeable {
        ACTIVE("1"), INACTIVE("ACTIVE");

        private final String code;

        Status(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getTitle() {
            return name();
        }
    }
}
