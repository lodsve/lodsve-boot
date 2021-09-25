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

import com.lodsve.boot.component.mybatis.repository.bean.IdColumn;
import com.lodsve.boot.component.mybatis.repository.helper.MapperHelper;
import com.lodsve.boot.component.mybatis.utils.MyBatisUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用Mapper模板类，扩展通用Mapper时需要继承该类.
 *
 * @author Hulk Sun
 */
public class BaseMapperProvider {
    private final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private final Map<String, Method> methodMap = new HashMap<>();
    private final Class<?> mapperClass;

    public BaseMapperProvider(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
    }

    /**
     * 获取执行的方法名
     *
     * @param ms ms
     * @return 方法名
     */
    private static String getMethodName(MappedStatement ms) {
        return getMethodName(ms.getId());
    }

    /**
     * 获取执行的方法名
     *
     * @param msId msId
     * @return 方法名
     */
    private static String getMethodName(String msId) {
        return msId.substring(msId.lastIndexOf(".") + 1);
    }

    /**
     * 根据msId获取接口类
     *
     * @param msId msId
     * @return 接口类
     */
    private static Class<?> getMapperClass(String msId) {
        if (!msId.contains(MapperHelper.STRING_POINT)) {
            throw new RuntimeException("当前MappedStatement的id=" + msId + ",不符合MappedStatement的规则!");
        }
        String mapperClassStr = msId.substring(0, msId.lastIndexOf("."));
        try {
            return Class.forName(mapperClassStr);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String dynamicSQL(Object record) {
        return MapperHelper.PROVIDER_METHOD_NAME;
    }

    /**
     * 是否支持该通用方法
     *
     * @param msId msId
     * @return 是否支持该通用方法
     */
    public boolean supportMethod(String msId) {
        Class<?> mapperClass = getMapperClass(msId);
        if (mapperClass != null && this.mapperClass.isAssignableFrom(mapperClass)) {
            String methodName = getMethodName(msId);
            return methodMap.get(methodName) != null;
        }
        return false;
    }

    /**
     * 重新设置SqlSource
     *
     * @param ms        ms
     * @param parameter 参数
     */
    public void resetSqlSource(MappedStatement ms, MapperMethod.ParamMap parameter) {
        Method method = methodMap.get(getMethodName(ms));

        if (method.getReturnType() == Void.TYPE) {
            invoke(method, this, ms, parameter);
        } else if (SqlNode.class.isAssignableFrom(method.getReturnType())) {
            SqlNode sqlNode = (SqlNode) invoke(method, this, ms, parameter);
            DynamicSqlSource dynamicSqlSource = new DynamicSqlSource(ms.getConfiguration(), sqlNode);
            setSqlSource(ms, dynamicSqlSource);
        } else if (String.class.equals(method.getReturnType())) {
            String xmlSql = (String) invoke(method, this, ms, parameter);
            SqlSource sqlSource = createSqlSource(ms, xmlSql);
            //替换原有的SqlSource
            setSqlSource(ms, sqlSource);
        } else {
            throw new RuntimeException("自定义Mapper方法返回类型错误,可选的返回类型为void,SqlNode,String三种!");
        }
    }

    /**
     * 重新设置SqlSource
     *
     * @param ms        ms
     * @param sqlSource sqlSource
     */
    private void setSqlSource(MappedStatement ms, SqlSource sqlSource) {
        MyBatisUtils.setValue(ms, "sqlSource", sqlSource);
    }

    private Object invoke(Method method, Object target, MappedStatement ms, MapperMethod.ParamMap parameter) {
        try {
            return method.invoke(target, ms);
        } catch (Exception e) {
            try {
                return method.invoke(target, ms, parameter);
            } catch (IllegalAccessException | InvocationTargetException e1) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 创建SqlSource
     *
     * @param ms     ms
     * @param xmlSql xmlSql
     * @return SqlSource
     */
    private SqlSource createSqlSource(MappedStatement ms, String xmlSql) {
        return languageDriver.createSqlSource(ms.getConfiguration(), "<script>\n\t" + xmlSql + "</script>", null);
    }

    /**
     * 添加映射方法
     *
     * @param methodName methodName
     * @param method     method
     */
    public void addMethodMap(String methodName, Method method) {
        methodMap.put(methodName, method);
    }

    /**
     * 获取返回值类型 - 实体类型
     *
     * @param ms ms
     * @return 实体类型
     */
    public Class<?> getSelectReturnType(MappedStatement ms) {
        String msId = ms.getId();
        Class<?> mapperClass = getMapperClass(msId);
        if (mapperClass == null) {
            throw new RuntimeException("无法获取Mapper<T>泛型类型:" + msId);
        }

        Type[] types = mapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                if (t.getRawType() == this.mapperClass || this.mapperClass.isAssignableFrom((Class<?>) t.getRawType())) {
                    return (Class<?>) t.getActualTypeArguments()[0];
                }
            }
        }
        throw new RuntimeException("无法获取Mapper<T>泛型类型:" + msId);
    }

    /**
     * 通过反射设置主键字段
     *
     * @param idColumn 主键字段
     * @param ms       MappedStatement
     */
    public void setKeyColumn(IdColumn idColumn, MappedStatement ms) {
        if (StringUtils.isBlank(idColumn.getProperty())) {
            return;
        }

        MyBatisUtils.setValue(ms, "keyProperties", new String[]{idColumn.getProperty()});
    }
}
