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
package com.lodsve.boot.component.openfeign.client.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有分页请求接口均继承该类.
 *
 * @author Hulk Sun
 */
public class BaseRequestPageableDTO extends BaseRequestDTO {
    /**
     * 当前页码，默认为0
     */
    private static final Integer DEFAULT_PAGE = 0;
    /**
     * 每页记录数，默认为10
     */
    private static final Integer DEFAULT_SIZE = 10;

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 每页记录数
     */
    private Integer size;
    /**
     * 排序
     */
    private List<Sort.Order> orders;

    @JsonIgnore
    public void setPageable(Pageable pageable) {
        setPage(pageable.getPageNumber());
        setSize(pageable.getPageSize());
        Sort sorts = pageable.getSort();

        List<Sort.Order> os = new ArrayList<>();
        for (Sort.Order sort : sorts) {
            Sort.Order order = new Sort.Order(sort.getDirection(), sort.getProperty());
            os.add(order);
        }
        setOrders(os);
    }

    @JsonIgnore
    public Pageable getPageable() {
        Integer page = getPage() == null ? DEFAULT_PAGE : getPage();
        Integer size = getSize() == null ? DEFAULT_SIZE : getSize();

        if (getOrders() == null) {
            return PageRequest.of(page, size);
        }

        return PageRequest.of(page, size, Sort.by(getOrders()));
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Sort.Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Sort.Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
