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

import com.lodsve.boot.component.mybatis.pojo.BasePropertyPO;

import java.time.LocalDateTime;

/**
 * 设置基本字段,供修改或新增使用.
 *
 * @author Hulk Sun
 */
public class BasePropertyUtil {
    private BasePropertyUtil() {
    }

    /**
     * 新增使用
     *
     * @param baseProperty baseProperty
     * @param createdBy    createdBy
     */
    public static void initCreatedInfo(BasePropertyPO baseProperty, Long createdBy) {
        LocalDateTime now = LocalDateTime.now();

        // 创建人 创建时间
        baseProperty.setCreatedBy(createdBy);
        baseProperty.setCreatedDate(now);

        // 修改人 修改时间
        baseProperty.setLastModifiedBy(createdBy);
        baseProperty.setLastModifiedDate(now);

        // 是否可用
        baseProperty.setEnabled(1);
        baseProperty.setDisabledDate(LocalDateTime.of(1900, 1, 1, 0, 0, 0));

        // 乐观锁
        baseProperty.setVersion(1);
    }

    /**
     * 修改使用
     *
     * @param baseProperty   baseProperty
     * @param lastModifiedBy lastModifiedBy
     */
    public static void initLastModifiedInfo(BasePropertyPO baseProperty, Long lastModifiedBy) {
        LocalDateTime now = LocalDateTime.now();

        // 更新人 更新时间
        baseProperty.setLastModifiedBy(lastModifiedBy);
        baseProperty.setLastModifiedDate(now);
    }

    /**
     * 逻辑删除的时候使用
     *
     * @param baseProperty   baseProperty
     * @param lastModifiedBy lastModifiedBy
     */
    public static void initDisableInfo(BasePropertyPO baseProperty, Long lastModifiedBy) {
        LocalDateTime now = LocalDateTime.now();

        // 更新人 更新时间
        baseProperty.setLastModifiedBy(lastModifiedBy);
        baseProperty.setLastModifiedDate(now);

        // 删除状态 删除时间
        baseProperty.setEnabled(0);
        baseProperty.setDisabledDate(now);
    }
}
