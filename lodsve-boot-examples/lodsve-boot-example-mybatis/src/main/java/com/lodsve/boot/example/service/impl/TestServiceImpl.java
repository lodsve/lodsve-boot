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
package com.lodsve.boot.example.service.impl;

import com.lodsve.boot.component.mybatis.utils.BasePropertyUtil;
import com.lodsve.boot.component.rdbms.annotations.SwitchDataSource;
import com.lodsve.boot.example.dao.TestDAO;
import com.lodsve.boot.example.pojo.form.TestForm;
import com.lodsve.boot.example.pojo.po.TestPO;
import com.lodsve.boot.example.pojo.query.TestQuery;
import com.lodsve.boot.example.pojo.vo.TestVO;
import com.lodsve.boot.example.service.TestService;
import com.lodsve.boot.utils.BeanMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * .
 *
 * @author Hulk Sun
 */
@Service
public class TestServiceImpl implements TestService {
    private final TestDAO testDAO;

    public TestServiceImpl(TestDAO testDAO) {
        this.testDAO = testDAO;
    }

    @Override
    @SwitchDataSource("test")
    public TestVO loadTest(Long id) {
        TestPO test = testDAO.findById(id);
        if (null == test) {
            return null;
        }
        return BeanMapper.map(test, TestVO.class);
    }

    @Override
    @SwitchDataSource("test")
    public boolean save(TestForm testForm) {
        if (null == testForm) {
            return false;
        }
        if (testForm.isCreate()) {
            // 创建
            TestPO test = new TestPO();
            test.setName(testForm.getName());
            test.setAge(testForm.getAge());
            test.setSex(testForm.getSex());
            test.setRemarks("create");

            BasePropertyUtil.initCreatedInfo(test, 1L);
            testDAO.save(test);
            return true;
        } else {
            // 编辑
            TestPO test = testDAO.findById(testForm.getId());
            if (null == test) {
                return false;
            }
            test.setName(testForm.getName());
            test.setAge(testForm.getAge());
            test.setSex(testForm.getSex());
            test.setRemarks("update");

            BasePropertyUtil.initLastModifiedInfo(test, 1L);
            testDAO.updateAll(test);
            return true;
        }
    }

    @Override
    @SwitchDataSource("test")
    public Page<TestVO> findAll(Pageable pageable, TestQuery query) {
        return testDAO.findAll(pageable, query).map(d -> {
            TestVO test = new TestVO();
            test.setId(d.getId());
            test.setName(d.getName());
            test.setAge(d.getAge());
            test.setSex(d.getSex());
            test.setRemarks(d.getRemarks());
            return test;
        });
    }

    @Override
    @SwitchDataSource("test")
    public boolean delete(Long id) {
        return testDAO.deleteById(id);
    }

    @Override
    @SwitchDataSource("test")
    public boolean logicDelete(Long id) {
        return testDAO.logicDeleteById(id);
    }
}
