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
import com.lodsve.boot.component.mybatis.repository.annotations.LogicDelete;
import com.lodsve.boot.component.mybatis.repository.helper.MapperHelper;
import com.lodsve.boot.component.mybatis.repository.provider.MapperProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.MappedStatement;

import java.io.Serializable;

/**
 * 基础删除类DAO，定义了一些基础的删除方法.
 * 其中的方法与{@link MapperProvider}一一对应
 * eg:
 * <pre>
 *  &#64;Repository
 *  public interface DemoDAO extends BaseDeleteRepository&lt;Demo&gt; {
 *  }
 * </pre>
 *
 * @author Hulk Sun
 * @see MapperProvider
 */
public interface BaseDeleteRepository<T extends BasePO> {
    /**
     * 根据主键字段进行删除，方法参数必须包含完整的主键属性
     *
     * @param id 主键
     * @return 操作结果
     * @see MapperProvider#deleteById(MappedStatement)
     */
    @DeleteProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    boolean deleteById(Serializable id);

    /**
     * 逻辑删除，需要在逻辑删除字段添加注解{@link LogicDelete}.
     *
     * @param id 主键
     * @return 操作结果
     * @see MapperProvider#logicDeleteById(MappedStatement)
     */
    @UpdateProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    boolean logicDeleteById(Serializable id);

    /**
     * 逻辑删除，需要在逻辑删除字段添加注解{@link LogicDelete}.
     *
     * @param id             主键
     * @param lastModifiedBy 更新人字段
     * @return 操作结果
     * @see MapperProvider#logicDeleteByIdWithModifiedBy(MappedStatement)
     */
    @UpdateProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    boolean logicDeleteByIdWithModifiedBy(Serializable id, Long lastModifiedBy);
}
