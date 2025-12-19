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
package com.lodsve.boot.example.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:41
 */
public class User implements Serializable {
    private Long id;
    private String userName;
    private String telNo;
    private Date createAt;

    /**
     * 获取用户编号.
     *
     * @return 用户编号
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户编号.
     *
     * @param id 用户编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名.
     *
     * @return 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名.
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取电话号码.
     *
     * @return 电话号码
     */
    public String getTelNo() {
        return telNo;
    }

    /**
     * 设置电话号码.
     *
     * @param telNo 电话号码
     */
    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    /**
     * 获取创建时间.
     *
     * @return 创建时间
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * 设置创建时间.
     *
     * @param createAt 创建时间
     */
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
