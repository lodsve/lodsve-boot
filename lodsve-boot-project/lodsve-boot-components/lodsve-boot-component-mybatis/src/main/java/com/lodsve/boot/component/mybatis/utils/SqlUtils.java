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

import com.google.common.collect.Sets;
import com.lodsve.boot.component.mybatis.utils.format.SqlFormatter;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.WithItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * sql utils.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public final class SqlUtils {
    public static final String KEEP_ORDER_BY = "/*keep orderby*/";
    private static final SqlFormatter SQL_FORMATTER = new SqlFormatter();
    private static final Alias TABLE_ALIAS;

    /**
     * 聚合函数
     */
    private static final Set<String> SKIP_FUNCTIONS = Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> FALSE_FUNCTIONS = Collections.synchronizedSet(new HashSet<>());

    /**
     * 聚合函数，以下列函数开头的都认为是聚合函数
     */
    private static final Set<String> AGGREGATE_FUNCTIONS = Sets.newHashSet(
        "APPROX_COUNT_DISTINCT",
        "ARRAY_AGG",
        "AVG",
        "BIT_",
        "BOOL_",
        "CHECKSUM_AGG",
        "COLLECT",
        "CORR",
        "COUNT",
        "COVAR",
        "CUME_DIST",
        "DENSE_RANK",
        "EVERY",
        "FIRST",
        "GROUP",
        "JSON_",
        "LAST",
        "LISTAGG",
        "MAX",
        "MEDIAN",
        "MIN",
        "PERCENT_",
        "RANK",
        "REGR_",
        "SELECTIVITY",
        "STATS_",
        "STD",
        "STRING_AGG",
        "SUM",
        "SYS_OP_ZONE_ID",
        "SYS_XMLAGG",
        "VAR",
        "XMLAGG");

    static {
        TABLE_ALIAS = new Alias("table_count");
        TABLE_ALIAS.setUseAs(false);
    }

    /**
     * 格式sql
     *
     * @param boundSql 原sql
     * @param format   是否格式化
     * @return 格式化后的sql
     */
    public static String sqlFormat(String boundSql, boolean format) {
        if (format) {
            return SQL_FORMATTER.format(boundSql);
        } else {
            return boundSql.replaceAll("[\\s]+", " ");
        }
    }

    /**
     * 添加到聚合函数，可以是逗号隔开的多个函数前缀
     *
     * @param functions 需要添加的聚合函数，逗号隔开
     */
    public static void addAggregateFunctions(String functions) {
        if (StringUtils.isEmpty(functions)) {
            return;
        }

        String[] temp = functions.split(",");
        Arrays.asList(temp).forEach(f -> AGGREGATE_FUNCTIONS.add(f.toUpperCase()));
    }

    /**
     * 获取单行的countSql
     *
     * @param sql 原始sql
     * @return 单行的countSql
     */
    public static String getSingleLineCountSql(String sql) {
        return getSingleLineCountSql(sql, "0");
    }

    /**
     * 获取单行的countSql
     *
     * @param sql  原始sql
     * @param name 列名，默认 0
     * @return 单行的countSql
     */
    public static String getSingleLineCountSql(@NonNull String sql, @NonNull String name) {
        //解析SQL
        Statement stmt;
        //特殊sql不需要去掉order by时，使用注释前缀
        if (sql.startsWith(KEEP_ORDER_BY)) {
            sql = StringUtils.removeStart(sql, KEEP_ORDER_BY);
            return getSimpleCountSql(sql);
        }
        try {
            stmt = CCJSqlParserUtil.parse(sql);
        } catch (Throwable e) {
            //无法解析的用一般方法返回count语句
            return getSimpleCountSql(sql);
        }
        Select select = (Select) stmt;
        SelectBody selectBody = select.getSelectBody();
        try {
            //处理body-去order by
            processSelectBody(selectBody);
        } catch (Exception e) {
            //当 sql 包含 group by 时，不去除 order by
            return getSimpleCountSql(sql);
        }
        //处理with-去order by
        processWithItemsList(select.getWithItemsList());
        //处理为count查询
        sqlToCount(select, name);
        return select.toString();
    }

    /**
     * 获取普通的Count-sql
     *
     * @param sql 原查询sql
     * @return 返回count查询sql
     */
    public static String getSimpleCountSql(@NonNull final String sql) {
        return getSimpleCountSql(sql, "0");
    }

    /**
     * 获取普通的Count-sql
     *
     * @param sql  原查询sql
     * @param name 字段名
     * @return 返回count查询sql
     */
    public static String getSimpleCountSql(@NonNull final String sql, @NonNull String name) {
        String countSql = "SELECT COUNT(%s) FROM (%s) tmp_count";
        return String.format(countSql, name, sql);
    }

    /**
     * 将sql转换为count查询
     *
     * @param select select
     * @param name   name
     */
    private static void sqlToCount(Select select, String name) {
        SelectBody selectBody = select.getSelectBody();
        // 是否能简化count查询
        List<SelectItem> countItem = new ArrayList<>();
        countItem.add(new SelectExpressionItem(new Column("COUNT(" + name + ")")));
        if (selectBody instanceof PlainSelect && isSimpleCount((PlainSelect) selectBody)) {
            ((PlainSelect) selectBody).setSelectItems(countItem);
        } else {
            PlainSelect plainSelect = new PlainSelect();
            SubSelect subSelect = new SubSelect();
            subSelect.setSelectBody(selectBody);
            subSelect.setAlias(TABLE_ALIAS);
            plainSelect.setFromItem(subSelect);
            plainSelect.setSelectItems(countItem);
            select.setSelectBody(plainSelect);
        }
    }

    /**
     * 是否可以用简单的count查询方式
     *
     * @param select select
     * @return true/false
     */
    private static boolean isSimpleCount(PlainSelect select) {
        //包含group by的时候不可以
        if (select.getGroupByColumnReferences() != null) {
            return false;
        }
        //包含distinct的时候不可以
        if (select.getDistinct() != null) {
            return false;
        }
        for (SelectItem item : select.getSelectItems()) {
            //select列中包含参数的时候不可以，否则会引起参数个数错误
            if (item.toString().contains("?")) {
                return false;
            }
            //如果查询列中包含函数，也不可以，函数可能会聚合列
            if (item instanceof SelectExpressionItem) {
                Expression expression = ((SelectExpressionItem) item).getExpression();
                if (expression instanceof Function) {
                    String name = ((Function) expression).getName();
                    if (StringUtils.isBlank(name)) {
                        continue;
                    }

                    if (SKIP_FUNCTIONS.contains(name)) {
                        continue;
                    }

                    String upperName = name.toUpperCase();
                    if (FALSE_FUNCTIONS.contains(upperName)) {
                        return false;
                    } else {
                        for (String aggregateFunction : AGGREGATE_FUNCTIONS) {
                            if (name.startsWith(aggregateFunction)) {
                                FALSE_FUNCTIONS.add(name);
                                return false;
                            }
                        }
                        SKIP_FUNCTIONS.add(name);
                    }
                }
            }
        }
        return true;
    }

    /**
     * 处理selectBody去除Order by
     *
     * @param selectBody selectBody
     */
    private static void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<SelectBody> plainSelects = operationList.getSelects();
                for (SelectBody plainSelect : plainSelects) {
                    processSelectBody(plainSelect);
                }
            }
            if (orderByHashParameters(operationList.getOrderByElements())) {
                operationList.setOrderByElements(null);
            }
        }
    }

    /**
     * 处理PlainSelect类型的selectBody
     *
     * @param plainSelect plainSelect
     */
    private static void processPlainSelect(PlainSelect plainSelect) {
        if (orderByHashParameters(plainSelect.getOrderByElements())) {
            plainSelect.setOrderByElements(null);
        }
        if (plainSelect.getFromItem() != null) {
            processFromItem(plainSelect.getFromItem());
        }
        if (plainSelect.getJoins() != null && plainSelect.getJoins().size() > 0) {
            List<Join> joins = plainSelect.getJoins();
            for (Join join : joins) {
                if (join.getRightItem() != null) {
                    processFromItem(join.getRightItem());
                }
            }
        }
    }

    /**
     * 处理WithItem
     *
     * @param withItems withItems
     */
    private static void processWithItemsList(List<WithItem> withItems) {
        if (CollectionUtils.isEmpty(withItems)) {
            return;
        }

        withItems.forEach(item -> processSelectBody(item.getSelectBody()));
    }

    /**
     * 处理子查询
     *
     * @param fromItem fromItem
     */
    private static void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoinList() != null && subJoin.getJoinList().size() > 0) {
                for (Join join : subJoin.getJoinList()) {
                    if (join.getRightItem() != null) {
                        processFromItem(join.getRightItem());
                    }
                }
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
        //Table时不用处理
    }

    /**
     * 判断Order by是否包含参数，有参数的不能去
     *
     * @param orderByElements orderByElements
     * @return true/false
     */
    private static boolean orderByHashParameters(List<OrderByElement> orderByElements) {
        if (CollectionUtils.isEmpty(orderByElements)) {
            return true;
        }

        return orderByElements.stream().noneMatch(ele -> ele.toString().contains("?"));
    }

    /**
     * 将驼峰风格替换为下划线风格
     *
     * @param str 驼峰风格字段名or表名
     * @return 下划线风格
     */
    public static String camelHumpToUnderline(String str) {
        final int size;
        final char[] chars;
        final StringBuilder sb = new StringBuilder(
            (size = (chars = str.toCharArray()).length) * 3 / 2 + 1);
        char c;
        for (int i = 0; i < size; i++) {
            c = chars[i];
            if (isUppercaseAlpha(c)) {
                sb.append('_').append(c);
            } else {
                sb.append(toUpperAscii(c));
            }
        }
        return sb.charAt(0) == '_' ? sb.substring(1) : sb.toString();
    }

    private static boolean isUppercaseAlpha(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    private static char toUpperAscii(char c) {
        if (isUppercaseAlpha(c)) {
            c -= (char) 0x20;
        }
        return c;
    }
}
