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
package com.lodsve.boot.component.mybatis.plugins.repository;

import com.lodsve.boot.component.mybatis.repository.BaseRepository;
import com.lodsve.boot.component.mybatis.repository.bean.DeleteColumn;
import com.lodsve.boot.component.mybatis.repository.bean.DisabledDateColumn;
import com.lodsve.boot.component.mybatis.repository.bean.EntityTable;
import com.lodsve.boot.component.mybatis.repository.bean.IdColumn;
import com.lodsve.boot.component.mybatis.repository.bean.LastModifiedByColumn;
import com.lodsve.boot.component.mybatis.repository.bean.LastModifiedDateColumn;
import com.lodsve.boot.component.mybatis.repository.helper.EntityHelper;
import com.lodsve.boot.component.mybatis.repository.helper.MapperHelper;
import com.lodsve.boot.component.mybatis.repository.provider.BaseMapperProvider;
import com.lodsve.boot.component.mybatis.repository.provider.ExternalProvider;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.Assert;

import java.sql.SQLSyntaxErrorException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * 通用Mapper拦截器.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 15/7/13 下午4:31
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class BaseRepositoryInterceptor implements Interceptor {
    private static final String LOGIC_DELETE_WITH_MODIFIED_BY_MAPPED_STATEMENT_ID = "logicDeleteByIdWithModifiedBy";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] objects = invocation.getArgs();
        MappedStatement ms = (MappedStatement) objects[0];
        // 参数
        ParamMap parameter = ((objects[1] instanceof ParamMap) ? (ParamMap) objects[1] : null);
        String msId = ms.getId();
        //不需要拦截的方法直接返回
        if (MapperHelper.isMapperMethod(msId)) {
            // 第一次经过处理后，就不会是ProviderSqlSource了，一开始高并发时可能会执行多次，但不影响。以后就不会在执行了
            if (ms.getSqlSource() instanceof ProviderSqlSource) {
                MapperHelper.resetSqlSource(ms, parameter);
            }
        }

        // 要排除logicDeleteWithModifiedBy这个方法，因为这个方法每次都会修改参数，所以得每次都从这边走
        if (msId.contains(LOGIC_DELETE_WITH_MODIFIED_BY_MAPPED_STATEMENT_ID)) {
            handleParams(ms, parameter);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        MapperHelper.registerMapper(BaseRepository.class);

        ServiceLoader<ExternalProvider> serviceLoader = ServiceLoader.load(ExternalProvider.class);
        serviceLoader.iterator().forEachRemaining(obj -> obj.provider().stream().filter(Class::isInterface).forEach(MapperHelper::registerMapper));

        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private void handleParams(MappedStatement ms, ParamMap parameter) throws SQLSyntaxErrorException {
        Assert.notNull(parameter, "parameter must be non-null!");

        BaseMapperProvider mapperProvider = MapperHelper.getMapperProvider(ms.getId());

        Class<?> entityClass = mapperProvider.getSelectReturnType(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        DeleteColumn deleteColumn = table.getDeleteColumn();
        if (null == deleteColumn) {
            throw new SQLSyntaxErrorException("不支持逻辑删除！没有@LogicDelete注解");
        }

        IdColumn idColumn = table.getIdColumn();
        LastModifiedByColumn modifiedByColumn = table.getModifiedByColumn();
        LastModifiedDateColumn modifiedDateColumn = table.getModifiedDateColumn();
        DisabledDateColumn disabledDateColumn = table.getDisabledDateColumn();

        handleParamMap(parameter, idColumn, modifiedByColumn, modifiedDateColumn, disabledDateColumn);
    }


    @SuppressWarnings("unchecked")
    private void handleParamMap(ParamMap parameter, IdColumn idColumn, LastModifiedByColumn modifiedByColumn, LastModifiedDateColumn modifiedDateColumn, DisabledDateColumn disabledDateColumn) {
        // 修改参数
        ParamMap<Object> paramMap = (ParamMap<Object>) parameter;
        Object id = parameter.get("arg0");
        Object modifiedBy = parameter.get("arg1");
        paramMap.remove("arg0");
        paramMap.remove("arg1");
        paramMap.remove("param1");
        paramMap.remove("param2");
        LocalDateTime now = LocalDateTime.now();

        if (modifiedByColumn != null) {
            paramMap.put(modifiedByColumn.getProperty(), modifiedBy);
        }
        if (modifiedDateColumn != null) {
            paramMap.put(modifiedDateColumn.getProperty(), now);
        }
        if (disabledDateColumn != null) {
            paramMap.put(disabledDateColumn.getProperty(), now);
        }
        paramMap.put(idColumn.getProperty(), id);
    }
}
