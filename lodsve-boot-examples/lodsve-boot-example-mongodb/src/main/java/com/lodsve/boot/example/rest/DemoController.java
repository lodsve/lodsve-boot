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

import com.lodsve.boot.example.domain.Demo;
import com.lodsve.boot.example.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version 1.0 2017/12/7 16:24
 */
@Tag(name = "MongoDB测试", description = "MongoDB测试")
@RestController
@RequestMapping("/demo")
public class DemoController {
    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @Operation(summary = "保存到demo", description = "保存到demo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "保存成功")})
    @RequestMapping(value = "/saveDemo", method = RequestMethod.POST)
    public void save2(@Parameter(name = "demo", description = "需要保存的") @RequestBody Demo demo) {
        demoService.save(demo);
    }

    @Operation(summary = "从demo获取", description = "从demo获取")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "获取成功")})
    @RequestMapping(value = "/demo/{id}", method = RequestMethod.GET)
    public Demo get2(@Parameter(name = "id", description = "ID") @PathVariable("id") Long id) {
        return demoService.get(id);
    }

    @Operation(summary = "获取demo全部", description = "获取demo全部")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "获取全部成功")})
    @RequestMapping(value = "/listDemo", method = RequestMethod.GET)
    public List<Demo> list2() {
        return demoService.list();
    }
}
