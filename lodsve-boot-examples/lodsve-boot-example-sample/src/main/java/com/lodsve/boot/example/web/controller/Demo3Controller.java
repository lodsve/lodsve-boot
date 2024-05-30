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
package com.lodsve.boot.example.web.controller;

import com.lodsve.boot.component.webmvc.response.ResultWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ResultWrapper
@RestController
@RequestMapping("/demo3")
public class Demo3Controller {
    @GetMapping("/t1")
    public Integer t1() {
        return 1;
    }

    @GetMapping("/t2")
    public Boolean t2() {
        return true;
    }

    @GetMapping("/t3")
    public Long t3() {
        return 1L;
    }

    @GetMapping("/t4")
    public Float t4() {
        return 1F;
    }

    @GetMapping("/t5")
    public Double t5() {
        return 1D;
    }

    @GetMapping("/t6")
    public int t6() {
        return 1;
    }
}
