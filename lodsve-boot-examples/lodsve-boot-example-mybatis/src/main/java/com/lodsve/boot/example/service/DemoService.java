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
