package com.lodsve.boot.example.dao;

import com.lodsve.boot.component.mybatis.repository.BaseRepository;
import com.lodsve.boot.example.pojo.po.DemoPO;
import com.lodsve.boot.example.pojo.query.DemoQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * .
 *
 * @author Hulk Sun
 */
@Repository
public interface DemoDAO extends BaseRepository<DemoPO> {
    /**
     * 分页查询
     *
     * @param pageable 分页对象
     * @param query    查询条件
     * @return 分页数据
     */
    Page<DemoPO> findAll(Pageable pageable, DemoQuery query);
}
