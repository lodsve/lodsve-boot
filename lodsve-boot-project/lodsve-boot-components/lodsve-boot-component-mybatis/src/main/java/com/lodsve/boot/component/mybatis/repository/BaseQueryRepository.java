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
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.MappedStatement;

import java.io.Serializable;
import java.util.List;

/**
 * 基础查询类DAO，定义了一些基础的查询方法.
 * 其中的方法与{@link MapperProvider}一一对应
 * eg:
 * <pre>
 *  &#64;Repository
 *  public interface DemoDAO extends BaseQueryRepository&lt;Demo&gt; {
 *  }
 * </pre>
 *
 * @author Hulk Sun
 * @see MapperProvider
 */
public interface BaseQueryRepository<T extends BasePO> {
    /**
     * 根据实体中的id属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param id 主键值
     * @return 查询到的值
     * @see MapperProvider#findById(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    T findById(Serializable id);

    /**
     * 根据实体中的id属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     * 这个方法只获取加了{@link LogicDelete}注解的字段值为{@link LogicDelete#nonDelete()}的记录
     * 如果没有加这个注解，这个方法的作用与{@link #findById(Serializable)}一致！
     *
     * @param id 主键值
     * @return 查询到的值
     * @see MapperProvider#findEnabledById(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    T findEnabledById(Serializable id);

    /**
     * 根据传入的主键集合，查询出对象的集合(不会按照软删除来查询，查询条件仅仅为主键匹配)
     *
     * @param ids 主键集合
     * @return 主键匹配的对象的集合
     * @see MapperProvider#findByIds(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    List<T> findByIds(List<? extends Serializable> ids);

    /**
     * 根据实体中的id属性进行查询，查询出对象的集合查询条件使用等号
     * 这个方法只获取加了{@link LogicDelete}注解的字段值为{@link LogicDelete#nonDelete()}的记录
     * 如果没有加这个注解，这个方法的作用与{@link #findById(Serializable)}一致！
     *
     * @param ids 主键集合
     * @return 主键匹配的对象的集合
     * @see MapperProvider#findEnabledByIds(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    List<T> findEnabledByIds(List<? extends Serializable> ids);

    /**
     * 查询总条数
     *
     * @return 数据库记录总条数
     * @see MapperProvider#count(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    int count();

    /**
     * 查询总条数
     * 这个方法只获取加了{@link LogicDelete}注解的字段值为{@link LogicDelete#nonDelete()}的记录
     * 如果没有加这个注解，这个方法的作用与{@link #count()}一致！
     *
     * @return 数据库记录总条数
     * @see MapperProvider#countEnabled(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    int countEnabled();

    /**
     * 判断是否存在
     *
     * @param id 主键值
     * @return 是否存在
     * @see MapperProvider#isExist(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    boolean isExist(Serializable id);

    /**
     * 判断是否存在(如果有逻辑删除，则添加这个条件，否则与{@link #isExist(Serializable)})效果一致
     *
     * @param id 主键值
     * @return 是否存在
     * @see MapperProvider#isExistEnabled(MappedStatement)
     */
    @SelectProvider(type = MapperProvider.class, method = MapperHelper.PROVIDER_METHOD_NAME)
    boolean isExistEnabled(Serializable id);
}
