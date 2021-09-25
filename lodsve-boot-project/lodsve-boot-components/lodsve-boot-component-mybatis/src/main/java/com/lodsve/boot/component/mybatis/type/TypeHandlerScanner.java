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
package com.lodsve.boot.component.mybatis.type;

import com.google.common.collect.Lists;
import com.lodsve.boot.bean.Codeable;
import com.lodsve.boot.component.mybatis.exception.MyBatisException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.LoaderClassPath;
import javassist.Modifier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 扫描所有的MyBatis的TypeHandler.
 *
 * @author Hulk Sun
 */
public class TypeHandlerScanner {
    private static final Logger logger = LoggerFactory.getLogger(TypeHandlerScanner.class);

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
    private final List<TypeFilter> includeFilters = new LinkedList<>();
    private final ClassPool pool;

    public TypeHandlerScanner() {
        this.includeFilters.add(new AssignableTypeFilter(Codeable.class));
        ClassPool parent = ClassPool.getDefault();
        ClassPool child = new ClassPool(parent);
        child.appendClassPath(new LoaderClassPath(TypeHandlerScanner.class.getClassLoader()));
        child.appendSystemPath();
        child.childFirstLookup = true;
        pool = child;
    }

    /**
     * 通过spring的SpEL扫描系统中配置的枚举类型转换器
     *
     * @param enumPackages 枚举类型所在的包路径，可以是多个
     * @return TypeHandler
     */
    public TypeHandler<?>[] find(String... enumPackages) {
        if (ArrayUtils.isEmpty(enumPackages)) {
            throw new MyBatisException("enums package is required!");
        }

        List<String> enumClasses = new ArrayList<>();
        try {
            for (String bp : enumPackages) {
                enumClasses.addAll(this.getEnumClasses(bp));
            }
        } catch (IOException e) {
            logger.error("扫描枚举类型发生错误", e);
            return new TypeHandler<?>[0];
        }

        return getTypeHandlers(enumClasses).toArray(new TypeHandler<?>[0]);
    }

    private List<TypeHandler<?>> getTypeHandlers(List<String> classes) {
        List<TypeHandler<?>> typeHandlers = Lists.newArrayList();
        for (String enumClass : classes) {
            TypeHandler<?> handler = getTypeHandlerInstance(enumClass);
            if (handler != null) {
                typeHandlers.add(handler);
            }
        }

        return typeHandlers;
    }

    private TypeHandler<?> getTypeHandlerInstance(String enumClass) {
        try {
            Class<?> enumClazz = ClassUtils.forName(enumClass, Thread.currentThread().getContextClassLoader());

            //创建一个TypeHandler类
            CtClass typeHandler = pool.makeClass(enumClazz.getSimpleName() + "$TypeHandler$" + System.currentTimeMillis());
            //添加EnumCodeTypeHandler父类(不包含泛型)
            CtClass enumCodeTypeHandler = pool.get(AbstractEnumCodeTypeHandler.class.getName());
            typeHandler.setSuperclass(enumCodeTypeHandler);

            //创建构造器
            CtConstructor constructor = new CtConstructor(null, typeHandler);
            constructor.setModifiers(Modifier.PUBLIC);
            constructor.setBody("{super(" + enumClass + ".class);}");
            typeHandler.addConstructor(constructor);

            // 另取捷径,设置rawType的值
            // mybatis中,最后需要rawType(即枚举的类型)与handler对应
            Class<?> clazz = typeHandler.toClass();
            TypeHandler<?> handler = (TypeHandler<?>) clazz.newInstance();
            Field field = clazz.getSuperclass().getSuperclass().getSuperclass().getDeclaredField("rawType");
            field.setAccessible(true);
            field.set(handler, enumClazz);

            return handler;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private List<String> getEnumClasses(String basePackage) throws IOException {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;

        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        List<String> classes = new ArrayList<>(resources.length);
        for (Resource resource : resources) {
            MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
            if (isCodeableClass(metadataReader)) {
                classes.add(metadataReader.getClassMetadata().getClassName());
            }
        }

        return classes;
    }

    private boolean isCodeableClass(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }
}
