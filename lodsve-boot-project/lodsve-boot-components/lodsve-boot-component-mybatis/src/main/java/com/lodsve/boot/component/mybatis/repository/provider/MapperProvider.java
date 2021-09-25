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

import com.lodsve.boot.component.mybatis.repository.BaseRepository;
import com.lodsve.boot.component.mybatis.repository.annotations.LogicDelete;
import com.lodsve.boot.component.mybatis.repository.bean.ColumnBean;
import com.lodsve.boot.component.mybatis.repository.bean.DeleteColumn;
import com.lodsve.boot.component.mybatis.repository.bean.DisabledDateColumn;
import com.lodsve.boot.component.mybatis.repository.bean.EntityTable;
import com.lodsve.boot.component.mybatis.repository.bean.IdColumn;
import com.lodsve.boot.component.mybatis.repository.bean.LastModifiedByColumn;
import com.lodsve.boot.component.mybatis.repository.bean.LastModifiedDateColumn;
import com.lodsve.boot.component.mybatis.repository.bean.VersionColumn;
import com.lodsve.boot.component.mybatis.repository.helper.EntityHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;

import java.io.Serializable;
import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper实现类，基础方法实现类.其中的方法与{@link BaseRepository}一一对应.
 *
 * @author Hulk Sun
 * @see BaseRepository
 */
public class MapperProvider extends BaseMapperProvider {

    public MapperProvider(Class<?> mapperClass) {
        super(mapperClass);
    }

    /**
     * 根据实体中的id属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param ms MappedStatement
     * @return 根据实体中的id属性进行查询Sql
     * @see BaseRepository#findById(Serializable)
     */
    public String findById(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();
        Set<ColumnBean> columnBeans = table.getColumns();

        String columns = columnBeans.stream().map(ColumnBean::getColumn).collect(Collectors.joining(", "));

        return String.format("SELECT %s FROM %s WHERE %s", columns, table.getName(), idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}");
    }

    /**
     * 根据实体中的id属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     * 这个方法只获取加了{@link LogicDelete}注解的字段值为{@link LogicDelete#nonDelete()}的记录
     * 如果没有加这个注解，这个方法的作用与{@link BaseRepository#findById(Serializable)}一致！
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#findEnabledById(Serializable)
     */
    public String findEnabledById(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();
        DeleteColumn deleteColumn = table.getDeleteColumn();
        Set<ColumnBean> columnBeans = table.getColumns();

        String columns = columnBeans.stream().map(ColumnBean::getColumn).collect(Collectors.joining(", "));

        String whereSql = idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}";
        if (deleteColumn != null) {
            whereSql += " AND " + deleteColumn.getColumn() + " = " + deleteColumn.getNonDelete();
        }

        return String.format("SELECT %s FROM %s WHERE %s", columns, table.getName(), whereSql);
    }

    /**
     * 根据传入的主键集合，查询出对象的集合(不会按照软删除来查询，查询条件仅仅为主键匹配)
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#findByIds(List)
     */
    public String findByIds(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();
        Set<ColumnBean> columnBeans = table.getColumns();

        String columns = columnBeans.stream().map(ColumnBean::getColumn).collect(Collectors.joining(", "));
        String idIns = "<foreach collection=\"list\" item=\"item\" separator=\",\" open=\"(\" close=\")\">#{item}</foreach>";

        return String.format("SELECT %s FROM %s WHERE %s IN %s", columns, table.getName(), idColumn.getColumn(), idIns);
    }

    /**
     * 根据实体中的id属性进行查询，查询出对象的集合查询条件使用等号
     * 这个方法只获取加了{@link LogicDelete}注解的字段值为{@link LogicDelete#nonDelete()}的记录
     * 如果没有加这个注解，这个方法的作用与{@link #findByIds(MappedStatement)} 一致！
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#findEnabledByIds(List)
     */
    public String findEnabledByIds(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();
        DeleteColumn deleteColumn = table.getDeleteColumn();
        Set<ColumnBean> columnBeans = table.getColumns();

        String columns = columnBeans.stream().map(ColumnBean::getColumn).collect(Collectors.joining(", "));
        String idIns = "<foreach collection=\"list\" item=\"item\" separator=\",\" open=\"(\" close=\")\">#{item}</foreach>";

        String whereSql = idColumn.getColumn() + " IN " + idIns;
        if (deleteColumn != null) {
            whereSql += " AND " + deleteColumn.getColumn() + " = " + deleteColumn.getNonDelete();
        }

        return String.format("SELECT %s FROM %s WHERE %s", columns, table.getName(), whereSql);
    }

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值。
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#save(Object)
     */
    public String save(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        Set<ColumnBean> columnBeans = table.getColumns();

        String columns = columnBeans.stream().map(ColumnBean::getColumn).collect(Collectors.joining(", "));
        String values = columnBeans.stream().map(c -> "#{" + c.getProperty() + "}").collect(Collectors.joining(", "));

        // 通过反射设置主键字段
        setKeyColumn(table.getIdColumn(), ms);

        return String.format("INSERT INTO %s (%s) VALUES (%s)", table.getName(), columns, values);
    }

    /**
     * 批量保存，保存后生成的主键会回填到每一个对象的主键字段
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#batchSave(List)
     */
    public String batchSave(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        Set<ColumnBean> columnBeans = table.getColumns();

        String columns = columnBeans.stream().map(ColumnBean::getColumn).collect(Collectors.joining(", "));
        String values = columnBeans.stream().map(c -> "#{item." + c.getProperty() + "}").collect(Collectors.joining(", "));
        String batchValues = "<foreach collection=\"list\" item=\"item\" separator=\",\" open=\"\" close=\"\">(%s)</foreach>";

        // 通过反射设置主键字段
        setKeyColumn(table.getIdColumn(), ms);

        return String.format("INSERT INTO %s (%s) VALUES %s", table.getName(), columns, String.format(batchValues, values));
    }

    /**
     * 根据主键更新所有属性的值。
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#updateAll(Object)
     */
    public String updateAll(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();
        VersionColumn versionColumn = table.getVersionColumn();
        Set<ColumnBean> columnBeans = table.getColumns();

        String columns = columnBeans.stream().filter(c -> {
            boolean isId = StringUtils.equals(idColumn.getProperty(), c.getProperty());
            boolean isVersion = versionColumn != null && StringUtils.equals(versionColumn.getProperty(), c.getProperty());

            return !isId && !isVersion;
        }).map(c -> c.getColumn() + " = #{" + c.getProperty() + "}").collect(Collectors.joining(", "));

        String sql = "UPDATE %s SET %s WHERE %s";

        return String.format(sql, table.getName(), columns, idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}");
    }

    /**
     * 根据主键更新属性不为null的值（String类型，应该还不为空字符串）。
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#update(Object)
     */
    public String update(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();
        VersionColumn versionColumn = table.getVersionColumn();
        Set<ColumnBean> columnBeans = table.getColumns();


        StringBuilder whereAndSetSql = new StringBuilder();
        whereAndSetSql.append("<trim prefix=\"SET\" suffixOverrides=\",\" suffix=\"WHERE ").append(idColumn.getColumn())
            .append(" = #{").append(idColumn.getProperty()).append("}\">");

        columnBeans.stream().filter(c -> {
            boolean isId = StringUtils.equals(idColumn.getProperty(), c.getProperty());
            boolean isVersion = versionColumn != null && StringUtils.equals(versionColumn.getProperty(), c.getProperty());

            return !isId && !isVersion;
        }).forEach(c -> {
            String ifSqlEvenColumn = String.format("<if test=\"%s != null\">%s, </if>", c.getProperty(), c.getColumn() + " = #{" + c.getProperty() + "}");
            if (String.class.equals(c.getJavaType())) {
                ifSqlEvenColumn = String.format("<if test=\"%s != null and %s != ''\">%s, </if>", c.getProperty(), c.getProperty(), c.getColumn() + " = #{" + c.getProperty() + "}");
            }

            whereAndSetSql.append(ifSqlEvenColumn);
        });
        whereAndSetSql.append("</trim>");

        String sql = "UPDATE %s %s";

        return String.format(sql, table.getName(), whereAndSetSql.toString());
    }

    /**
     * 根据主键字段进行删除，方法参数必须包含完整的主键属性！
     * 注意：这里是物理删除，慎用！
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#deleteById(Serializable)
     */
    public String deleteById(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();

        String sql = "DELETE FROM %s WHERE %s";
        return String.format(sql, table.getName(), idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}");
    }

    /**
     * 逻辑删除，需要在逻辑删除字段添加注解{@link LogicDelete}
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @throws SQLSyntaxErrorException SQLSyntaxErrorException
     * @see BaseRepository#logicDeleteById(Serializable)
     */
    public String logicDeleteById(MappedStatement ms) throws SQLSyntaxErrorException {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        DeleteColumn deleteColumn = table.getDeleteColumn();

        if (null == deleteColumn) {
            throw new SQLSyntaxErrorException("不支持逻辑删除！没有@LogicDelete注解");
        }
        IdColumn idColumn = table.getIdColumn();

        String sql = "UPDATE %s SET %s WHERE %s";
        return String.format(sql, table.getName(), deleteColumn.getColumn() + " = " + deleteColumn.getDelete(), idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}");
    }

    /**
     * 逻辑删除，需要在逻辑删除字段添加注解{@link LogicDelete}.
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @throws SQLSyntaxErrorException SQLSyntaxErrorException
     * @see BaseRepository#logicDeleteByIdWithModifiedBy(Serializable, Long)
     */
    public String logicDeleteByIdWithModifiedBy(MappedStatement ms) throws SQLSyntaxErrorException {
        EntityTable table = EntityHelper.getEntityTable(getSelectReturnType(ms));
        DeleteColumn deleteColumn = table.getDeleteColumn();
        if (null == deleteColumn) {
            throw new SQLSyntaxErrorException("不支持逻辑删除！没有@LogicDelete注解");
        }

        IdColumn idColumn = table.getIdColumn();
        LastModifiedDateColumn modifiedDateColumn = table.getModifiedDateColumn();
        LastModifiedByColumn modifiedByColumn = table.getModifiedByColumn();
        DisabledDateColumn disabledDateColumn = table.getDisabledDateColumn();

        String sqlTemplate = "UPDATE %s SET %s WHERE %s";

        // 逻辑删除
        StringBuilder setSql = new StringBuilder();
        setSql.append(deleteColumn.getColumn()).append(" = ").append(deleteColumn.getDelete()).append(", ");
        // 更新人
        if (modifiedByColumn != null) {
            setSql.append(modifiedByColumn.getColumn()).append(" = #{").append(modifiedByColumn.getProperty()).append("}, ");
        }
        // 更新时间、禁用时间
        if (modifiedDateColumn != null) {
            setSql.append(modifiedDateColumn.getColumn()).append(" = #{").append(modifiedDateColumn.getProperty()).append("}, ");
        }
        if (disabledDateColumn != null) {
            setSql.append(disabledDateColumn.getColumn()).append(" = #{").append(disabledDateColumn.getProperty()).append("}, ");
        }

        return String.format(sqlTemplate, table.getName(), setSql.substring(0, setSql.length() - 2), idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}");
    }

    /**
     * 查询总条数
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#count()
     */
    public String count(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);

        return String.format("SELECT COUNT(1) FROM %s", table.getName());
    }

    /**
     * 查询总条数
     * 这个方法只获取加了{@link LogicDelete}注解的字段值为{@link LogicDelete#nonDelete()}的记录
     * 如果没有加这个注解，这个方法的作用与{@link #count(MappedStatement)}一致！
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#countEnabled()
     */
    public String countEnabled(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        DeleteColumn deleteColumn = table.getDeleteColumn();

        return String.format("SELECT COUNT(1) FROM %s WHERE %s", table.getName(), deleteColumn.getColumn() + " = " + deleteColumn.getNonDelete());
    }

    /**
     * 判断是否存在
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#isExist(Serializable)
     */
    public String isExist(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();

        return String.format("SELECT COUNT(1) FROM %s WHERE %s", table.getName(), idColumn.getColumn() + " = #{" + idColumn.getProperty() + "}");
    }

    /**
     * 判断是否存在(如果有逻辑删除，则添加这个条件，否则与{@link #isExist(MappedStatement)})效果一致
     *
     * @param ms MappedStatement
     * @return 生成的SQL语句
     * @see BaseRepository#isExistEnabled(Serializable)
     */
    public String isExistEnabled(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        IdColumn idColumn = table.getIdColumn();
        DeleteColumn deleteColumn = table.getDeleteColumn();

        String whereSql = idColumn.getColumn() + " = #{" + idColumn.getProperty() + "} AND " + deleteColumn.getColumn() + " = " + deleteColumn.getNonDelete();
        return String.format("SELECT COUNT(1) FROM %s WHERE %s", table.getName(), whereSql);
    }
}
