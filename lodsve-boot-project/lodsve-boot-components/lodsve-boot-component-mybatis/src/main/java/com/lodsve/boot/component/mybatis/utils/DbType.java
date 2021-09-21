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

import org.springframework.util.Assert;

/**
 * 表示数据库类型的枚举.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public enum DbType {
    /**
     * 数据库类型
     */
    DB_ORACLE("O", "Oracle"), DB_MYSQL("M", "MySQL"), DB_HSQL("H", "HSQL Database Engine"), DB_SQL_SERVER("S", "SqlServer");

    /**
     * dbType
     */
    private final String dbType;
    /**
     * name
     */
    private final String name;

    /**
     * DbType
     *
     * @param dbType String
     * @param name   String
     */
    DbType(String dbType, String name) {
        this.dbType = dbType;
        this.name = name;
    }

    /**
     * Description: <br>
     *
     * @param eval String
     * @return <br>
     */
    public static DbType eval(String eval) {
        Assert.hasLength(eval, "eval is required!");

        for (DbType dt : DbType.values()) {
            if (eval.equals(dt.getDbType())) {
                return dt;
            }
        }

        return null;
    }

    /**
     * Description: <br>
     *
     * @return String
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * Description: <br>
     *
     * @return String
     */
    public String getName() {
        return name;
    }
}
