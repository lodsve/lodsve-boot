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
import com.lodsve.boot.component.mybatis.query.MyBatisSqlQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * mybatis分页工具类.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class PaginationUtils {
    private static final Pattern ORDER_BY = Pattern.compile(".*order\\s+by\\s+.*", Pattern.CASE_INSENSITIVE);

    private PaginationUtils() {
    }

    /**
     * 从MappedStatement的参数中指定类型的参数
     *
     * @param parameter parameter
     * @param target    target
     * @param <T>       类型
     * @return 指定类型的参数
     */
    @SuppressWarnings("unchecked")
    public static <T> T findObjectFromParameter(Object parameter, Class<T> target) {
        if (parameter == null || target == null) {
            return null;
        }

        if (target.isAssignableFrom(parameter.getClass())) {
            return (T) parameter;
        }

        if (parameter instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) parameter;
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                Object paramValue = entry.getValue();

                if (paramValue != null && target.isAssignableFrom(paramValue.getClass())) {
                    return (T) paramValue;
                }
            }
        }

        return null;
    }

    public static int queryForTotal(String sql, MappedStatement mappedStatement, BoundSql boundSql) throws Exception {
        if (StringUtils.isEmpty(sql)) {
            return 0;
        }

        try (MyBatisSqlQuery query = new MyBatisSqlQuery(mappedStatement)) {
            Dialect dialect = MyBatisUtils.getDialect();
            String totalSql = dialect.getCountSql(sql);
            BoundSql countBoundSql = copyFromBoundSql(mappedStatement, boundSql, totalSql);
            return query.queryForInt(countBoundSql);
        }
    }

    public static String getPageSql(String sql, long start, int num) {
        Assert.hasText(sql, "sql is required!");

        Dialect dialect = MyBatisUtils.getDialect();
        return dialect.getPageSql(sql, start, num);
    }

    public static MappedStatement copyFromNewSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql);

        return copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
    }

    private static BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    private static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        Builder builder = new Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        //setStatementTimeout()
        builder.timeout(ms.getTimeout());

        //setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        //setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        //setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    public static String applySortSql(String sql, Sort sort) {
        if (null == sort || !sort.iterator().hasNext()) {
            return sql;
        }

        StringBuilder builder = new StringBuilder(sql);

        if (!ORDER_BY.matcher(sql).matches()) {
            builder.append(" order by ");
        } else {
            builder.append(", ");
        }

        for (Sort.Order order : sort) {
            builder.append(getOrderClause(order)).append(", ");
        }

        builder.delete(builder.length() - 2, builder.length());

        return builder.toString();
    }

    private static String getOrderClause(Sort.Order order) {
        String property = order.getProperty();
        String wrapped = order.isIgnoreCase() ? String.format("lower(%s)", property) : property;

        return String.format("%s %s", wrapped, toSqlDirection(order));
    }

    private static String toSqlDirection(Sort.Order order) {
        return order.getDirection().name().toLowerCase(Locale.US);
    }

    private static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
