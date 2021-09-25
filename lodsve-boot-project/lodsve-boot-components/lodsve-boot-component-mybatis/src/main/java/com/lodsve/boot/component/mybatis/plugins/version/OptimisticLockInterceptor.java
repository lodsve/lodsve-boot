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
package com.lodsve.boot.component.mybatis.plugins.version;

import com.lodsve.boot.component.mybatis.repository.bean.EntityTable;
import com.lodsve.boot.component.mybatis.repository.bean.IdColumn;
import com.lodsve.boot.component.mybatis.repository.bean.VersionColumn;
import com.lodsve.boot.component.mybatis.repository.helper.EntityHelper;
import com.lodsve.boot.component.mybatis.utils.MyBatisUtils;
import com.lodsve.boot.component.mybatis.utils.VersionUtils;
import com.lodsve.boot.utils.GenericUtils;
import com.vip.vjtools.vjkit.reflect.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 乐观锁插件
 *
 * @author sunhao
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
    @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class OptimisticLockInterceptor implements Interceptor {
    private static final String PREPARE = "prepare";
    private static final String SET_PARAMETERS = "setParameters";
    private static final String UPDATE = "update";

    private static final String SQL_REGEX_PREFIX = "[\\s\\S]*";
    private static final String SQL_REGEX_SUFFIX = "\\s*=\\s*\\?[\\s\\S]*";

    private static final String DELEGATE_MAPPED_STATEMENT_KEY = "delegate.mappedStatement";
    private static final String DELEGATE_BOUND_SQL_KEY = "delegate.boundSql";
    private static final String DELEGATE_BOUND_SQL_SQL_KEY = "delegate.boundSql.sql";
    private static final String BOUND_SQL = "boundSql";
    private static final String MAPPED_STATEMENT = "mappedStatement";

    @Override
    public Object intercept(Invocation invocation) throws Exception {
        String interceptMethod = invocation.getMethod().getName();
        if (PREPARE.equals(interceptMethod)) {
            return handleMethodPrepare(invocation);
        } else if (SET_PARAMETERS.equals(interceptMethod)) {
            return handleMethodSetParameters(invocation);
        } else if (UPDATE.equals(interceptMethod)) {
            return handleMethodUpdate(invocation);
        }
        return invocation.proceed();
    }

    private Object handleMethodPrepare(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        StatementHandler statementHandler = (StatementHandler) MyBatisUtils.processTarget(invocation.getTarget());
        MappedStatement mappedStatement = MyBatisUtils.getValue(statementHandler, DELEGATE_MAPPED_STATEMENT_KEY);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        if (sqlCommandType != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }
        BoundSql boundSql = MyBatisUtils.getValue(statementHandler, DELEGATE_BOUND_SQL_KEY);
        if (!checkVersion(boundSql, mappedStatement)) {
            return invocation.proceed();
        }

        EntityTable table = getEntityTableForLogicDelete(boundSql, mappedStatement);
        if (table == null) {
            return invocation.proceed();
        }

        VersionColumn versionColumn = table.getVersionColumn();
        String versionColumnName = versionColumn.getColumn();

        String originalSql = MyBatisUtils.getValue(statementHandler, DELEGATE_BOUND_SQL_SQL_KEY);
        String sql = String.format("%s AND %s = ?", originalSql, versionColumnName);
        String replaceSql = StringUtils.replacePattern(sql, "\\s+(?i)set\\s+", " SET " + versionColumnName + " = " + versionColumnName + " + 1, ");
        MyBatisUtils.setValue(statementHandler, DELEGATE_BOUND_SQL_SQL_KEY, replaceSql);
        return invocation.proceed();
    }

    @SuppressWarnings("unchecked")
    private Object handleMethodSetParameters(Invocation invocation) throws InvocationTargetException, IllegalAccessException, SQLException {
        ParameterHandler parameterHandler = (ParameterHandler) MyBatisUtils.processTarget(invocation.getTarget());
        MappedStatement mappedStatement = MyBatisUtils.getValue(parameterHandler, MAPPED_STATEMENT);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }
        Configuration configuration = mappedStatement.getConfiguration();
        BoundSql boundSql = MyBatisUtils.getValue(parameterHandler, BOUND_SQL);
        if (!checkVersion(boundSql, mappedStatement)) {
            return invocation.proceed();
        }

        EntityTable table = getEntityTableForLogicDelete(boundSql, mappedStatement);
        if (table == null) {
            return invocation.proceed();
        }

        VersionColumn versionColumn = table.getVersionColumn();
        String versionColumnName = versionColumn.getColumn();

        ParameterMapping parameterMapping = new ParameterMapping.Builder(configuration, versionColumnName, versionColumn.getJavaType()).build();
        TypeHandler typeHandler = parameterMapping.getTypeHandler();

        Object currentVersion = getCurrentVersion(mappedStatement, table, boundSql.getParameterObject());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        try {
            PreparedStatement ps = (PreparedStatement) invocation.getArgs()[0];
            typeHandler.setParameter(ps, parameterMappings.size() + 1, currentVersion, parameterMapping.getJdbcType());
        } catch (TypeException | SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMappings + ". Cause: " + e, e);
        }

        return invocation.proceed();
    }

    private Object handleMethodUpdate(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        Object result = invocation.proceed();
        Object[] queryArgs = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) queryArgs[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType != SqlCommandType.UPDATE) {
            return result;
        }
        BoundSql boundSql = mappedStatement.getBoundSql(queryArgs[1]);
        if (!checkVersion(boundSql, mappedStatement)) {
            return result;
        }
        Object paramObj = boundSql.getParameterObject();

        EntityTable table = getEntityTableForLogicDelete(boundSql, mappedStatement);
        if (table == null) {
            return invocation.proceed();
        }

        VersionColumn versionColumn = table.getVersionColumn();
        String versionColumnName = versionColumn.getColumn();
        if (StringUtils.isNotBlank(versionColumnName)) {
            if (Integer.parseInt(result.toString()) == 0) {
                Field field = ReflectionUtil.getField(paramObj.getClass(), table.getIdColumn().getProperty());
                Object id = ReflectionUtil.getFieldValue(paramObj, field);

                throw new OptimisticLockException(infoString(paramObj.getClass().getName(), id.toString()));
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ParameterHandler || target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private Boolean checkVersion(BoundSql boundSql, MappedStatement mappedStatement) {
        EntityTable table = getEntityTableForLogicDelete(boundSql, mappedStatement);

        if (null == table) {
            // 类没有加@Table注解，不需要进行乐观锁
            return false;
        }

        String sql = boundSql.getSql();
        if (!sql.matches(SQL_REGEX_PREFIX + table.getIdColumn().getColumn() + SQL_REGEX_SUFFIX)) {
            return false;
        }

        return null != table.getVersionColumn();
    }

    private EntityTable getEntityTableForLogicDelete(BoundSql boundSql, MappedStatement mappedStatement) {
        Object paramObj = boundSql.getParameterObject();
        try {
            return EntityHelper.getEntityTable(paramObj.getClass());
        } catch (RuntimeException ex) {
            // 逻辑删除特殊处理
            try {
                String resource = mappedStatement.getResource();
                String className = StringUtils.replace(StringUtils.remove(resource, ".java (best guess)"), "/", ".");
                Class clazz = Class.forName(className);

                Class<?> entityClass = GenericUtils.getGenericParameter0(clazz);
                return EntityHelper.getEntityTable(entityClass);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    private Object getCurrentVersion(MappedStatement mappedStatement, EntityTable table, Object paramObj) throws SQLException {
        IdColumn idColumn = table.getIdColumn();
        Class<?> entityType = table.getEntityType();

        Field field = ReflectionUtil.getField(entityType, idColumn.getProperty());
        Object id;
        try {
            id = ReflectionUtil.getFieldValue(paramObj, field);
        } catch (Exception e) {
            if (paramObj instanceof MapperMethod.ParamMap) {
                id = ((MapperMethod.ParamMap) paramObj).get(idColumn.getProperty());
            } else {
                id = paramObj;
            }
        }

        return VersionUtils.getCurrentVersion(mappedStatement, table, Collections.singletonMap(idColumn.getProperty(), id));
    }

    /**
     * Generate an info message string relating to a particular entity,
     * based on the given entityName and id.
     *
     * @param entityName The defined entity name.
     * @param id         The entity id value.
     * @return An info string, in the form [FooBar#1].
     */
    private String infoString(String entityName, Serializable id) {
        StringBuilder s = new StringBuilder();
        s.append('[');
        if (entityName == null) {
            s.append("<null entity name>");
        } else {
            s.append(entityName);
        }
        s.append('#');

        if (id == null) {
            s.append("<null>");
        } else {
            s.append(id);
        }
        s.append(']');

        return s.toString();
    }
}
