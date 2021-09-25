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
package com.lodsve.boot.component.mybatis.repository.provider;

/**
 * 空方法Mapper接口默认MapperTemplate
 * 如BaseSelectMapper，接口纯继承，不包含任何方法.
 *
 * @author Hulk Sun
 */
public class EmptyMapperProvider extends BaseMapperProvider {

    public EmptyMapperProvider(Class<?> mapperClass) {
        super(mapperClass);
    }

    @Override
    public boolean supportMethod(String msId) {
        return false;
    }
}
