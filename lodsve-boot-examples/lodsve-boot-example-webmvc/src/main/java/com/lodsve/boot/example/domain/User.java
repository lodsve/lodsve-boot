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

import com.lodsve.boot.example.enums.Gender;
import com.lodsve.boot.example.enums.Status;
import com.lodsve.boot.example.enums.Test;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:41
 */
@ApiModel(description = "用户domain")
public class User implements Serializable {
    private Long id;
    private String userName;
    private String telNo;
    private Gender sex;
    private Status status;
    private Date createAt;
    private Test test;

    public User(String userName, String telNo) {
        this.id = 1L;
        this.userName = userName;
        this.telNo = telNo;
        this.sex = Gender.FEMALE;
        this.status = Status.AUDITING;
        this.createAt = new Date();
        this.test = Test.test1;
    }

    @ApiModelProperty(value = "编号", required = true)
    public Long getId() {
        return id;
    }

    @ApiModelProperty(value = "编号", required = true)
    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(value = "用户名", required = true)
    public String getUserName() {
        return userName;
    }

    @ApiModelProperty(value = "用户名", required = true)
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ApiModelProperty(value = "电话号码", required = true)
    public String getTelNo() {
        return telNo;
    }

    @ApiModelProperty(value = "电话号码", required = true)
    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    @ApiModelProperty(value = "性别", required = true)
    public Gender getSex() {
        return sex;
    }

    @ApiModelProperty(value = "性别", required = true)
    public void setSex(Gender sex) {
        this.sex = sex;
    }

    @ApiModelProperty(value = "状态", required = true)
    public Status getStatus() {
        return status;
    }

    @ApiModelProperty(value = "状态", required = true)
    public void setStatus(Status status) {
        this.status = status;
    }

    @ApiModelProperty(value = "创建时间", required = true)
    public Date getCreateAt() {
        return createAt;
    }

    @ApiModelProperty(value = "创建时间", required = true)
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }
}
