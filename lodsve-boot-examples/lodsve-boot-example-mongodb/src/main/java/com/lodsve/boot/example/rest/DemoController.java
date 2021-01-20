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
