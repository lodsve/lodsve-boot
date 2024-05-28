package com.lodsve.boot.example.service;

import com.lodsve.boot.example.pojo.form.TestForm;
import com.lodsve.boot.example.pojo.query.TestQuery;
import com.lodsve.boot.example.pojo.vo.TestVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * .
 *
 * @author Hulk Sun
 */
public interface TestService {
    /**
     * load test
     *
     * @param id id
     * @return test
     */
    TestVO loadTest(Long id);

    /**
     * 保存
     *
     * @param testForm test form
     * @return success?
     */
    boolean save(TestForm testForm);

    /**
     * 分页查询列表，带有查询
     *
     * @param pageable 分页信息
     * @param query    查询参数
     * @return 列表数据
     */
    Page<TestVO> findAll(Pageable pageable, TestQuery query);

    /**
     * 物理删除
     *
     * @param id test id
     * @return 是否物理删除成功
     */
    boolean delete(Long id);

    /**
     * 逻辑删除
     *
     * @param id test id
     * @return 是否逻辑删除成功
     */
    boolean logicDelete(Long id);
}
