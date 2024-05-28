package com.lodsve.boot.example.dao;

import com.lodsve.boot.component.mybatis.repository.BaseRepository;
import com.lodsve.boot.example.pojo.po.TestPO;
import com.lodsve.boot.example.pojo.query.TestQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * .
 *
 * @author Hulk Sun
 */
@Repository
public interface TestDAO extends BaseRepository<TestPO> {
    /**
     * 分页查询
     *
     * @param pageable 分页对象
     * @param query    查询条件
     * @return 分页数据
     */
    Page<TestPO> findAll(Pageable pageable, TestQuery query);
}
