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
package com.lodsve.boot.autoconfigure.rdbms;

import lombok.Getter;
import lombok.Setter;

import java.util.Properties;

/**
 * 连接池设置.
 *
 * @author Hulk Sun
 */
@Setter
@Getter
public class PoolSetting {
    /**
     * 使用哪种连接池，目前仅Hikari和Druid
     * 默认使用Hikari
     */
    private PoolType type = PoolType.Hikari;
    /**
     * 连接池初始化大小
     */
    private int initSize;

    /**
     * 连接池中的最大活动连接数
     */
    private int maxActive;

    /**
     * 连接池中的最小空闲连接数
     */
    private int minIdle;

    /**
     * 从连接池获取连接的最大等待时间
     */
    private int maxWait;

    /**
     * 用于验证连接有效性的查询语句
     */
    private String validationQuery;

    /**
     * 在获取连接时检测连接的有效性
     */
    private boolean testOnBorrow;

    /**
     * 在归还连接时检测连接的有效性
     */
    private boolean testOnReturn;

    /**
     * 在连接空闲时检测连接的有效性
     */
    private boolean testWhileIdle;

    /**
     * 连接清理器运行间隔时间
     */
    private int timeBetweenEvictionRunsMillis;

    /**
     * 连接在池中最小生存时间
     */
    private int minEvictableIdleTimeMillis;

    /**
     * 是否移除泄漏的连接
     */
    private boolean removeAbandoned;

    /**
     * 移除泄漏连接的超时时间
     */
    private int removeAbandonedTimeout;

    /**
     * 是否记录移除泄漏连接的日志
     */
    private boolean logAbandoned;

    /**
     * 数据库连接的其他属性
     */
    private Properties extProperties;

    public enum PoolType {
        /**
         * Hikari
         */
        Hikari,
        /**
         * Druid
         */
        Druid;
    }
}
