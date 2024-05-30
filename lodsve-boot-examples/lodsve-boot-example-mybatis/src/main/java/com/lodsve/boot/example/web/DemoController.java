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

import com.lodsve.boot.example.pojo.form.DemoForm;
import com.lodsve.boot.example.pojo.query.DemoQuery;
import com.lodsve.boot.example.pojo.vo.DemoVO;
import com.lodsve.boot.example.service.DemoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author Hulk Sun
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/{id}")
    public DemoVO loadUser(@PathVariable("id") Long id) {
        return demoService.loadDemo(id);
    }

    @PatchMapping("/")
    public boolean update(@RequestBody DemoForm demoForm) {
        return demoService.save(demoForm);
    }

    @PostMapping("/")
    public boolean save(@RequestBody DemoForm demoForm) {
        return demoService.save(demoForm);
    }

    @PostMapping("/list")
    public Page<DemoVO> findAll(@PageableDefault Pageable pageable, @RequestBody DemoQuery query) {
        return demoService.findAll(pageable, query);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return demoService.delete(id);
    }

    @DeleteMapping("/logic-delete/{id}")
    public boolean logicDelete(@PathVariable("id") Long id) {
        return demoService.logicDelete(id);
    }
}
