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
package com.lodsve.boot.example.api;

import com.lodsve.boot.component.openfeign.client.base.RpcResult;
import com.lodsve.boot.component.openfeign.client.pojo.BaseResponsePageDTO;
import com.lodsve.boot.example.config.PathConstant;
import com.lodsve.boot.example.dto.request.UserRequestDTO;
import com.lodsve.boot.example.dto.response.UserDataResponseDTO;
import com.lodsve.boot.example.dto.response.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * .
 *
 * @author Hulk Sun
 */
@FeignClient(path = PathConstant.CONTEXT_PATH + PathConstant.USER, name = PathConstant.NAME_APPLICATION)
public interface UserRpcApi {
    /**
     * 查询单条.
     *
     * @param id id
     * @return 单条记录
     */
    @GetMapping("/{id}")
    RpcResult<UserDataResponseDTO> load(@PathVariable Long id);

    /**
     * 查询全量.
     *
     * @return 全量
     */
    @GetMapping("/all")
    RpcResult<UserResponseDTO> all();

    /**
     * 查询分页
     *
     * @param request request
     * @return page object
     */
    @PostMapping("/list")
    RpcResult<BaseResponsePageDTO<UserDataResponseDTO>> list(@RequestBody UserRequestDTO request);
}
