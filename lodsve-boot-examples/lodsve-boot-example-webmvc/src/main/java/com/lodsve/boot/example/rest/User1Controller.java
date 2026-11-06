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
package com.lodsve.boot.example.rest;

import com.lodsve.boot.component.security.annotation.Authn;
import com.lodsve.boot.component.security.annotation.Authz;
import com.lodsve.boot.component.security.annotation.CurrentAccount;
import com.lodsve.boot.component.security.core.Account;
import com.lodsve.boot.example.domain.User;
import com.lodsve.boot.example.dto.UserDTO;
import com.lodsve.boot.example.enums.Gender;
import com.lodsve.boot.example.service.AuthzImpl;
import com.lodsve.boot.example.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:42
 */
@Tag(name = "Mybatis的CRUD测试", description = "Mybatis的CRUD测试")
@RestController
@RequestMapping("/group1/user")
public class User1Controller {
    @Autowired
    private UserService userService;

    @Operation(summary = "登录", description = "登录")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "登录成功")})
    @GetMapping
    public void login() {
        AuthzImpl.LOGIN = true;
    }

    @Operation(summary = "获取用户", description = "获取用户")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "获取用户成功")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Authn
    public User loadUser(@Parameter(name = "id", description = "用户ID") @PathVariable("id") Long id, @CurrentAccount Account account) {
        System.out.println(account);
        return this.userService.loadUser(id);
    }

    @Operation(summary = "删除用户", description = "删除用户")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "删除用户成功")})
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @Authz("admin")
    public boolean delete(@Parameter(name = "id", description = "用户ID") @RequestParam("id") Long id) {
        return this.userService.delete(id);
    }

    @Operation(summary = "更新用户", description = "更新用户")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "更新用户成功")})
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public boolean update(@Parameter(name = "userDto", description = "需要更新的用户") @RequestBody UserDTO userDto) {
        return this.userService.update(userDto) != null;
    }

    @Operation(summary = "保存用户", description = "保存审核用户")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "审核用户成功")})
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public User save(@Parameter(name = "userDto", description = "需要保存的用户") @RequestBody UserDTO userDto) {
        return this.userService.save(userDto);
    }

    @Operation(summary = "列出用户", description = "列出用户")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "列出用户成功")})
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page<User> findAll(@Parameter(name = "pageable", description = "分页信息,传参方式：?page=0&size=10") @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return this.userService.findAll(pageable);
    }

    @Operation(summary = "测试枚举", description = "测试枚举")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "测试枚举")})
    @RequestMapping(value = "/test/enums", method = RequestMethod.GET)
    public Gender testEnums(@Parameter(name = "gender", description = "性别") @RequestParam("gender") Gender gender) {
        return gender;
    }

    @Operation(summary = "测试时间", description = "测试时间")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "测试时间")})
    @RequestMapping(value = "/test/date", method = RequestMethod.GET)
    public Date testDate(@Parameter(name = "date", description = "时间") @RequestParam("date") Date date) {
        return date;
    }
}
