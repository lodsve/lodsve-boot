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
package com.lodsve.boot.component.mybatis.utils;

import com.lodsve.boot.component.mybatis.dialect.Dialect;
import com.lodsve.boot.component.mybatis.dialect.HsqlDialect;
import com.lodsve.boot.component.mybatis.dialect.MySqlDialect;
import com.lodsve.boot.component.mybatis.dialect.OracleDialect;
import com.lodsve.boot.component.mybatis.dialect.SqlServerDialect;
import com.lodsve.boot.component.mybatis.exception.MyBatisException;
import com.lodsve.boot.component.mybatis.query.NativeSqlQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * MyBatis utils.
 *
 * @author Hulk Sun
 */
public final class MyBatisUtils {
    private static final Logger logger = LoggerFactory.getLogger(MyBatisUtils.class);
    public static Method method;
    private static DbType dbType;

    static {
        try {
            Class<?> metaClass = Class.forName("org.apache.ibatis.reflection.SystemMetaObject");
            method = metaClass.getDeclaredMethod("forObject", Object.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new MyBatisException(e.getMessage());
        }

    }

    private MyBatisUtils() {
    }

    public static DbType getDbType() {
        if (null != dbType) {
            return dbType;
        }

        throw new MyBatisException("can't find DbType!");
    }

    /**
     * 根据数据源中的databaseProductName来判断是哪种类型数据库
     *
     * @param dbName databaseProductName
     */
    public static void setDbType(String dbName) {
        if (StringUtils.equalsIgnoreCase(dbName, "")) {
            throw new MyBatisException("When setting the database type, the database product name is empty.");
        }

        DbType dbType;
        if (StringUtils.containsIgnoreCase(dbName, "mysql")) {
            dbType = DbType.DB_MYSQL;
        } else if (StringUtils.containsIgnoreCase(dbName, "oracle")) {
            dbType = DbType.DB_ORACLE;
        } else if (StringUtils.containsIgnoreCase(dbName, "sqlserver")) {
            dbType = DbType.DB_SQL_SERVER;
        } else if (StringUtils.containsIgnoreCase(dbName, "hsqldb")) {
            dbType = DbType.DB_HSQL;
        } else {
            dbType = DbType.DB_MYSQL;
        }

        setDbType(dbType);
    }

    /**
     * 设置方言，只能调用一次！
     *
     * @param dbType 数据库方言
     */
    public static void setDbType(DbType dbType) {
        if (null != MyBatisUtils.dbType) {
            // 只允许修改一次
            if (logger.isErrorEnabled()) {
                logger.error("dbType已存在！不允许再设置！");
            }
            return;
        }
        MyBatisUtils.dbType = dbType;
    }

    public static Dialect getDialect() {
        DbType dbType = getDbType();

        switch (dbType) {
            case DB_HSQL:
                return new HsqlDialect();
            case DB_MYSQL:
                return new MySqlDialect();
            case DB_ORACLE:
                return new OracleDialect();
            case DB_SQL_SERVER:
                return new SqlServerDialect();
            default:
                return new MySqlDialect();
        }
    }

    public static int queryForInt(DataSource dataSource, String sql, Object... params) throws Exception {
        try (NativeSqlQuery query = new NativeSqlQuery(dataSource)) {
            return query.queryForInt(sql, params);
        }
    }

    private static MetaObject forObject(Object object) {
        return SystemMetaObject.forObject(object);
    }

    /**
     * 通过mybatis的MetaObject设置对象某个字段的值
     *
     * @param object    需要设置值得对象
     * @param fieldName 字段名
     * @param value     字段值
     */
    public static void setValue(Object object, String fieldName, Object value) {
        Assert.notNull(object, "object must be non-null!");

        MetaObject metaObject = MyBatisUtils.forObject(object);
        metaObject.setValue(fieldName, value);
    }

    /**
     * 通过mybatis的MetaObject获取对象某个字段的值
     *
     * @param object    需要获取值得对象
     * @param fieldName 字段名
     * @param <T>       值的类型
     * @return 某个字段的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(Object object, String fieldName) {
        Assert.notNull(object, "object must be non-null!");

        MetaObject metaObject = MyBatisUtils.forObject(object);
        return (T) metaObject.getValue(fieldName);
    }

    /**
     * <p>Recursive get the original target object.
     * <p>If integrate more than a plugin, maybe there are conflict in these plugins, because plugin will proxy the object.<br>
     * So, here get the orignal target object
     *
     * @param target proxy-object
     * @return original target object
     */
    public static Object processTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            return processTarget(getValue(target, "h.target"));
        }

        return target;
    }
}
