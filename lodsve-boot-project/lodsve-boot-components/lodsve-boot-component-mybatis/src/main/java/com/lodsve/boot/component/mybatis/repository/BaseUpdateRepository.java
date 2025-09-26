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
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * 基础更新类DAO，定义了一些基础的更新方法.
 * 其中的方法与{@link MapperProvider}一一对应
 * eg:
 * <pre>
 *  &#64;Repository
 *  public interface DemoDAO extends BaseUpdateRepository&lt;Demo&gt; {
 *  }
 * </pre>
 *
 * @author Hulk Sun
 * @see MapperProvider
 */
public interface BaseUpdateRepository<T extends BasePO> {
    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值。
     *
     * @param entity 需要保存的对象(主键为空则自动生成主键值,否则使用原主键值)
     * @return 操作后影响的数据库记录数量(一般情况为1)
     * @see MapperProvider#save(MappedStatement)
     */
    @InsertProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    @Options(useGeneratedKeys = true)
    int save(T entity);

    /**
     * 根据主键更新所有属性的值。
     *
     * @param entity 需要更新的对象,必须含有主键值
     * @return 操作后影响的数据库记录数量(一般情况为1)
     * @see MapperProvider#updateAll(MappedStatement)
     */
    @UpdateProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    int updateAll(T entity);

    /**
     * 根据主键更新属性不为null的值（String类型，应该还不为空字符串）。
     *
     * @param entity 需要更新的对象,必须含有主键值
     * @return 操作后影响的数据库记录数量(一般情况为1)
     * @see MapperProvider#update(MappedStatement)
     */
    @UpdateProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    int update(T entity);
}
