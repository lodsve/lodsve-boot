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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 所有分页响应对象均继承该类.
 *
 * @author Hulk Sun
 */
public class BaseResponsePageDTO<T> extends BaseResponseDTO {
    /**
     * 内容
     */
    private List<T> content;
    /**
     * 是否是第一页
     */
    private Boolean first;
    /**
     * 是否是最后一页
     */
    private Boolean last;
    /**
     * 当前页码
     */
    private Integer number;
    /**
     * 当前页数据条数
     */
    private Integer numberOfElements;
    /**
     * 每页显示的条数
     */
    private Integer size;
    /**
     * 总记录数
     */
    private Long totalElements;
    /**
     * 总页数
     */
    private Integer totalPages;
    /**
     * 暂存spring-data分页对象
     */
    @JsonIgnore
    private Page<T> page;


    /**
     * !!!!!!!!千万千万不要使用，留给反序列化使用的!!!!!!!!会爆炸!!!!!
     */
    public BaseResponsePageDTO() {
    }

    /**
     * 主要就是多这个默认构造器,和下面的set方法,供反序列化使用
     *
     * @param page spring-data Page
     * @see Page
     */
    public BaseResponsePageDTO(Page<T> page) {
        setPage(page);
        setContent(page.getContent());
        setFirst(page.isFirst());
        setLast(page.isLast());
        setNumber(page.getNumber());
        setNumberOfElements(page.getNumberOfElements());
        setSize(page.getSize());
        setTotalElements(page.getTotalElements());
        setTotalPages(page.getTotalPages());
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }

    public <U> BaseResponsePageDTO<U> map(Function<? super T, ? extends U> converter) {
        return new BaseResponsePageDTO<>(getPage().map(converter));
    }

    private <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {
        Assert.notNull(converter, "Function must not be null!");

        return content.stream().map(converter).collect(Collectors.toList());
    }

    /**
     * 转spring data page
     *
     * @return spring data page
     */
    public Page<T> toSpringPage() {
        return new PageImpl<>(getContent(), PageRequest.of(getNumber(), getSize()), getNumberOfElements());
    }

    /**
     * 转spring data page(包含类型,比如bo-&gt;vo)
     *
     * @param converter lambda转换器
     * @param <U>       目标对象
     * @return 目标对象的spring-data分页对象
     */
    public <U> Page<U> toSpringPage(Function<? super T, ? extends U> converter) {
        return new PageImpl<>(getConvertedContent(converter), PageRequest.of(getNumber(), getSize()), getNumberOfElements());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
