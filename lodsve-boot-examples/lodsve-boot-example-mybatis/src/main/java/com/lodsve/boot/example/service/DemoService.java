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

import com.lodsve.boot.example.pojo.form.DemoForm;
import com.lodsve.boot.example.pojo.query.DemoQuery;
import com.lodsve.boot.example.pojo.vo.DemoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * .
 *
 * @author Hulk Sun
 */
public interface DemoService {
    /**
     * load demo
     *
     * @param id id
     * @return demo
     */
    DemoVO loadDemo(Long id);

    /**
     * 保存
     *
     * @param demoForm demo form
     * @return success?
     */
    boolean save(DemoForm demoForm);

    /**
     * 分页查询列表，带有查询
     *
     * @param pageable 分页信息
     * @param query    查询参数
     * @return 列表数据
     */
    Page<DemoVO> findAll(Pageable pageable, DemoQuery query);

    /**
     * 物理删除
     *
     * @param id demo id
     * @return 是否物理删除成功
     */
    boolean delete(Long id);

    /**
     * 逻辑删除
     *
     * @param id demo id
     * @return 是否逻辑删除成功
     */
    boolean logicDelete(Long id);
}
