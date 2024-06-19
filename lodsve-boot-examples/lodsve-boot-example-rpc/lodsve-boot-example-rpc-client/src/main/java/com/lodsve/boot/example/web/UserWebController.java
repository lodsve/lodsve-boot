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
package com.lodsve.boot.example.web;

import com.lodsve.boot.example.pojo.query.UserQuery;
import com.lodsve.boot.example.pojo.vo.UserVO;
import com.lodsve.boot.bean.WebResult;
import com.lodsve.boot.component.openfeign.client.base.RpcResult;
import com.lodsve.boot.component.openfeign.client.base.RpcResultUtil;
import com.lodsve.boot.component.openfeign.client.pojo.BaseResponsePageDTO;
import com.lodsve.boot.example.api.UserRpcApi;
import com.lodsve.boot.example.dto.request.UserRequestDTO;
import com.lodsve.boot.example.dto.response.UserDataResponseDTO;
import com.lodsve.boot.example.dto.response.UserResponseDTO;
import com.lodsve.boot.utils.BeanMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * .
 *
 * @author Hulk Sun
 */
@RestController
@RequestMapping("/user")
public class UserWebController {
    private final UserRpcApi api;

    public UserWebController(UserRpcApi api) {
        this.api = api;
    }

    @GetMapping("/{id}")
    public WebResult<UserVO> load(@PathVariable Long id) {
        RpcResult<UserDataResponseDTO> result = api.load(id);
        UserDataResponseDTO response = RpcResultUtil.getRpcData(result);

        return WebResult.ok(BeanMapper.map(response, UserVO.class));
    }

    @GetMapping("/all")
    public WebResult<List<UserVO>> all() {
        RpcResult<UserResponseDTO> result = api.all();
        UserResponseDTO response = RpcResultUtil.getRpcData(result);
        List<UserDataResponseDTO> records = response.getRecords();

        List<UserVO> users = records.stream().map(rec -> BeanMapper.map(rec, UserVO.class)).collect(Collectors.toList());
        return WebResult.ok(users);
    }

    @PostMapping("/list")
    public WebResult<Page<UserVO>> list(@RequestBody UserQuery query, Pageable pageable) {
        UserRequestDTO request = new UserRequestDTO();
        request.setNameLike(query.getNameLike());
        request.setPageable(pageable);
        RpcResult<BaseResponsePageDTO<UserDataResponseDTO>> result = api.list(request);

        BaseResponsePageDTO<UserDataResponseDTO> response = RpcResultUtil.getRpcData(result);
        Page<UserVO> page = response.toSpringPage(data -> BeanMapper.map(data, UserVO.class));

        return WebResult.ok(page);
    }
}
