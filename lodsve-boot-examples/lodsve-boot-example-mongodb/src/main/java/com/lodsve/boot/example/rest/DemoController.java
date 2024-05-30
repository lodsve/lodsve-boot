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
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version 1.0 2017/12/7 16:24
 */
@Api(value = "MongoDB测试", description = "MongoDB测试")
@RestController
@RequestMapping("/demo")
public class DemoController {
    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @ApiOperation(value = "保存到demo", notes = "保存到demo")
    @ApiResponses({@ApiResponse(code = 200, message = "保存成功")})
    @RequestMapping(value = "/saveDemo", method = RequestMethod.POST)
    public void save2(@ApiParam(name = "demo", value = "需要保存的") @RequestBody Demo demo) {
        demoService.save(demo);
    }

    @ApiOperation(value = "从demo获取", notes = "从demo获取")
    @ApiResponses({@ApiResponse(code = 200, message = "获取成功")})
    @RequestMapping(value = "/demo/{id}", method = RequestMethod.GET)
    public Demo get2(@ApiParam(name = "id", value = "ID") @PathVariable("id") Long id) {
        return demoService.get(id);
    }

    @ApiOperation(value = "获取demo全部", notes = "获取demo全部")
    @ApiResponses({@ApiResponse(code = 200, message = "获取全部成功")})
    @RequestMapping(value = "/listDemo", method = RequestMethod.GET)
    public List<Demo> list2() {
        return demoService.list();
    }
}
