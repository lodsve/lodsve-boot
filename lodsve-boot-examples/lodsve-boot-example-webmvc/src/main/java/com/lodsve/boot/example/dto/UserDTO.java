/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.lodsve.boot.example.dto;

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
@ApiModel(description = "用户domain")
public class UserDTO {
    @ApiModelProperty(value = "编号", required = true)
    private Long id;
    @ApiModelProperty(value = "用户名", required = true)
    private String userName = StringUtils.EMPTY;
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
