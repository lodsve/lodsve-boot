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
package com.lodsve.boot.component.mybatis.pojo;

import com.lodsve.boot.component.mybatis.repository.annotations.DisabledDate;
import com.lodsve.boot.component.mybatis.repository.annotations.LogicDelete;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import java.time.LocalDateTime;

/**
 * 与数据库表结构一一对应，通过 DAO 层向上传输数据源对象
 * 有9个基本属性
 *
 * @author Hulk Sun
 */
public class BasePropertyPO extends BasePO {

    private static final long serialVersionUID = 5529749202040239279L;
    /**
     * id
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    /**
     * 上一次更新人
     */
    @LastModifiedBy
    @Column(name = "last_modified_by")
    private Long lastModifiedBy;
    /**
     * 上一次更新时间
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;
    /**
     * 是否可用，用来做逻辑删除 1:是 0否
     */
    @Column(name = "enabled")
    @LogicDelete
    private Integer enabled;
    /**
     * 版本号，用来做乐观锁
     */
    @Version
    @Column(name = "version")
    private Integer version;
    /**
     * 禁用时间
     */
    @DisabledDate
    @Column(name = "disabled_date")
    private LocalDateTime disabledDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getDisabledDate() {
        return disabledDate;
    }

    public void setDisabledDate(LocalDateTime disabledDate) {
        this.disabledDate = disabledDate;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
