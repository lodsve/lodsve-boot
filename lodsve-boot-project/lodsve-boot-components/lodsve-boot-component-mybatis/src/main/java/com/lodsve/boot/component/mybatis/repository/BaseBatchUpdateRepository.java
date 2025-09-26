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
package com.lodsve.boot.component.mybatis.repository;

import com.lodsve.boot.component.mybatis.pojo.BasePO;
import com.lodsve.boot.component.mybatis.repository.helper.MapperHelper;
import com.lodsve.boot.component.mybatis.repository.provider.MapperProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.List;

/**
 * 基础批量更新类DAO，定义了一些基础的批量更新方法.
 * 其中的方法与{@link MapperProvider}一一对应
 * eg:
 * <pre>
 *  &#64;Repository
 *  public interface DemoDAO extends BaseBatchUpdateRepository&lt;Demo&gt; {
 *  }
 * </pre>
 *
 * @author Hulk Sun
 * @see MapperProvider
 */
public interface BaseBatchUpdateRepository<T extends BasePO> {
    /**
     * 批量保存，保存后生成的主键会回填到每一个对象的主键字段
     *
     * @param entities 需要保存对象的集合
     * @return 保存成功的数据条数
     * @see MapperProvider#batchSave(MappedStatement)
     */
    @InsertProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    @Options(useGeneratedKeys = true)
    int batchSave(List<T> entities);
}
