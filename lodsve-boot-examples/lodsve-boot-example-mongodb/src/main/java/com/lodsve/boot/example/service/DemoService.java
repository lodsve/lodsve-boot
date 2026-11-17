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
package com.lodsve.boot.example.service;

import com.lodsve.boot.component.mongodb.dynamic.SwitchMongoDataSource;
import com.lodsve.boot.example.domain.Demo;
import com.lodsve.boot.example.repository.DemoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version 1.0 2017/12/7 16:00
 */
@Service
public class DemoService {
    private final DemoRepository demoRepository;

    public DemoService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    public Demo save(Demo demo) {
        return demoRepository.save(demo);
    }

    public Demo get(String id) {
        return demoRepository.findById(id).orElseThrow();
    }

    @SwitchMongoDataSource("archive")
    public Demo saveToArchive(Demo demo) {
        return demoRepository.save(demo);
    }

    @SwitchMongoDataSource("archive")
    public Demo getFromArchive(String id) {
        return demoRepository.findById(id).orElseThrow();
    }

    @SwitchMongoDataSource("archive")
    public List<Demo> listFromArchive() {
        return demoRepository.findAll();
    }

    public List<Demo> list() {
        return demoRepository.findAll();
    }
}
