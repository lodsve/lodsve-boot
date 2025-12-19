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
package com.lodsve.boot.example.dto;

import com.lodsve.boot.component.validator.annotations.Chinese;
import com.lodsve.boot.component.validator.annotations.Limit;
import com.lodsve.boot.component.validator.annotations.NotNull;
import com.lodsve.boot.component.validator.annotations.Telephone;
import com.lodsve.boot.component.validator.annotations.ValidateEntity;
import com.lodsve.boot.example.config.Equals;
import com.lodsve.boot.example.enums.Gender;
import com.lodsve.boot.example.enums.Status;
import com.lodsve.boot.example.enums.Test;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:43
 */
@ValidateEntity
@ApiModel(description = "用户domain")
public class UserDTO {
    @ApiModelProperty(value = "编号", required = true)
    @NotNull
    private Long id;
    @NotNull
    @Limit(min = 4, max = 10)
    @Chinese
    @ApiModelProperty(value = "用户名", required = true)
    @Equals(expect = "sunhao")
    private String userName = StringUtils.EMPTY;
    @Telephone
    @ApiModelProperty(value = "电话号码", required = true)
    private String telNo;
    @ApiModelProperty(value = "性别", required = true)
    private Gender sex = Gender.UNKNOWN;
    @ApiModelProperty(value = "状态", required = true)
    private Status status = Status.ENABLED;
    @ApiModelProperty(value = "测试", required = true)
    private Test test = Test.test1;

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

    @ApiModelProperty(value = "测试", required = true)
    public Test getTest() {
        return test;
    }

    @ApiModelProperty(value = "测试", required = true)
    public void setTest(Test test) {
        this.test = test;
    }
}
