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
package com.lodsve.boot.component.mybatis.repository.helper;

import com.lodsve.boot.component.mybatis.repository.provider.BaseMapperProvider;
import com.lodsve.boot.component.mybatis.repository.provider.EmptyMapperProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理主要逻辑，最关键的一个类.
 *
 * @author Hulk Sun
 */
public class MapperHelper {
    public static final String PROVIDER_METHOD_NAME = "dynamicSQL";
    public static final String STRING_POINT = ".";

    /**
     * 注册的通用Mapper接口
     */
    private static final Map<Class<?>, BaseMapperProvider> REGISTER_MAPPER = new ConcurrentHashMap<>();

    /**
     * 缓存msid和MapperTemplate
     */
    private static final Map<String, BaseMapperProvider> MS_ID_CACHE = new HashMap<>();
    /**
     * 缓存skip结果
     */
    private static final Map<String, Boolean> MS_ID_SKIP = new HashMap<>();

    /**
     * 默认构造方法
     */
    private MapperHelper() {
    }

    /**
     * 判断当前的接口方法是否需要进行拦截
     *
     * @param msId msId
     * @return true/false
     */
    public static boolean isMapperMethod(String msId) {
        if (MS_ID_SKIP.get(msId) != null) {
            return MS_ID_SKIP.get(msId);
        }
        for (Map.Entry<Class<?>, BaseMapperProvider> entry : REGISTER_MAPPER.entrySet()) {
            if (entry.getValue().supportMethod(msId)) {
                MS_ID_SKIP.put(msId, true);
                return true;
            }
        }
        MS_ID_SKIP.put(msId, false);
        return false;
    }

    /**
     * 重新设置SqlSource
     *
     * @param ms        MappedStatement
     * @param parameter 参数
     */
    public static void resetSqlSource(MappedStatement ms, MapperMethod.ParamMap parameter) {
        BaseMapperProvider baseMapperProvider = getMapperProvider(ms.getId());
        try {
            if (baseMapperProvider != null) {
                baseMapperProvider.resetSqlSource(ms, parameter);
            }
        } catch (Exception e) {
            throw new RuntimeException("调用方法异常:" + e.getMessage());
        }
    }

    /**
     * 获取MapperTemplate
     *
     * @param msId msId
     * @return MapperTemplate
     */
    public static BaseMapperProvider getMapperProvider(String msId) {
        BaseMapperProvider baseMapperProvider = null;
        if (MS_ID_CACHE.get(msId) != null) {
            baseMapperProvider = MS_ID_CACHE.get(msId);
        } else {
            for (Map.Entry<Class<?>, BaseMapperProvider> entry : REGISTER_MAPPER.entrySet()) {
                if (entry.getValue().supportMethod(msId)) {
                    baseMapperProvider = entry.getValue();
                    break;
                }
            }
            MS_ID_CACHE.put(msId, baseMapperProvider);
        }
        return baseMapperProvider;
    }

    /**
     * 注册通用Mapper接口
     *
     * @param mapperClass mapperClass
     */
    public static void registerMapper(Class<?> mapperClass) {
        if (!REGISTER_MAPPER.containsKey(mapperClass)) {
            REGISTER_MAPPER.put(mapperClass, fromMapperProvider(mapperClass));
        }
        //自动注册继承的接口
        Class<?>[] interfaces = mapperClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            registerMapper(anInterface);
        }
    }

    /**
     * 通过通用Mapper接口获取对应的MapperTemplate
     *
     * @param mapperClass mapperClass
     * @return MapperTemplate
     */
    private static BaseMapperProvider fromMapperProvider(Class<?> mapperClass) {
        Method[] methods = mapperClass.getDeclaredMethods();
        Class<?> templateClass = null;
        Class<?> tempClass = null;
        Set<String> methodSet = new HashSet<>();
        for (Method method : methods) {
            methodSet.add(method.getName());
            if (method.isAnnotationPresent(SelectProvider.class)) {
                SelectProvider provider = method.getAnnotation(SelectProvider.class);
                tempClass = provider.type();
            } else if (method.isAnnotationPresent(InsertProvider.class)) {
                InsertProvider provider = method.getAnnotation(InsertProvider.class);
                tempClass = provider.type();
            } else if (method.isAnnotationPresent(DeleteProvider.class)) {
                DeleteProvider provider = method.getAnnotation(DeleteProvider.class);
                tempClass = provider.type();
            } else if (method.isAnnotationPresent(UpdateProvider.class)) {
                UpdateProvider provider = method.getAnnotation(UpdateProvider.class);
                tempClass = provider.type();
            }

            if (templateClass == null) {
                templateClass = tempClass;
            } else if (templateClass != tempClass) {
                throw new RuntimeException("一个通用Mapper中只允许存在一个MapperTemplate子类!");
            }
        }
        if (templateClass == null || !BaseMapperProvider.class.isAssignableFrom(templateClass)) {
            templateClass = EmptyMapperProvider.class;
        }
        BaseMapperProvider mapperProvider;
        try {
            mapperProvider = (BaseMapperProvider) templateClass.getConstructor(Class.class).newInstance(mapperClass);
        } catch (Exception e) {
            throw new RuntimeException("实例化MapperTemplate对象失败:" + e.getMessage());
        }
        //注册方法
        for (String methodName : methodSet) {
            Method method;
            try {
                method = templateClass.getMethod(methodName, MappedStatement.class);
            } catch (NoSuchMethodException e) {
                try {
                    method = templateClass.getMethod(methodName, MappedStatement.class, Object.class);
                } catch (NoSuchMethodException e1) {
                    throw new RuntimeException(templateClass.getCanonicalName() + "中缺少" + methodName + "方法!");
                }
            }

            mapperProvider.addMethodMap(methodName, method);
        }
        return mapperProvider;
    }
}
