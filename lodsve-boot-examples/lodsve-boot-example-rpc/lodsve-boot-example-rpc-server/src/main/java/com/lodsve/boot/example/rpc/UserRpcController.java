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
package com.lodsve.boot.example.rpc;

import com.lodsve.boot.component.openfeign.client.base.RpcResult;
import com.lodsve.boot.component.openfeign.client.pojo.BaseResponsePageDTO;
import com.lodsve.boot.example.api.UserRpcApi;
import com.lodsve.boot.example.dto.request.UserRequestDTO;
import com.lodsve.boot.example.dto.response.UserDataResponseDTO;
import com.lodsve.boot.example.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * .
 *
 * @author Hulk Sun
 */
@RestController
@RequestMapping("/rpc/feign/user")
public class UserRpcController implements UserRpcApi {
    @Override
    @GetMapping("/{id}")
    public RpcResult<UserDataResponseDTO> load(@PathVariable Long id) {
        UserDataResponseDTO userData = new UserDataResponseDTO();
        userData.setId(id);
        userData.setUserName("username-" + id);
        userData.setSex(0 == id % 2 ? "男" : "女");
        userData.setTelNo("telNo---" + id);

        return new RpcResult<>(userData);
    }

    @Override
    @GetMapping("/all")
    public RpcResult<UserResponseDTO> all() {
        UserResponseDTO response = new UserResponseDTO();
        response.setRecords(build("username"));
        return new RpcResult<>(response);
    }

    @Override
    @PostMapping("/list")
    public RpcResult<BaseResponsePageDTO<UserDataResponseDTO>> list(@RequestBody UserRequestDTO request) {
        Pageable pageable = request.getPageable();
        String nameLike = request.getNameLike();

        List<UserDataResponseDTO> records = build(nameLike);
        Page<UserDataResponseDTO> page = new PageImpl<>(records, pageable, 11);
        return new RpcResult<>(new BaseResponsePageDTO<>(page));
    }

    private List<UserDataResponseDTO> build(String nameLike) {
        List<UserDataResponseDTO> records = new ArrayList<>(11);
        for (long i = 1L; i <= 11; i++) {
            UserDataResponseDTO userData = new UserDataResponseDTO();
            userData.setId(i);
            userData.setUserName(nameLike + "-" + i);
            userData.setSex(0 == i % 2 ? "男" : "女");
            userData.setTelNo("telNo---" + i);

            records.add(userData);
        }

        return records;
    }
}
