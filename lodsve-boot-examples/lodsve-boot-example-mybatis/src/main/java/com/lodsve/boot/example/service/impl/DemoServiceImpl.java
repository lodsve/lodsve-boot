package com.lodsve.boot.example.service.impl;

import com.lodsve.boot.component.mybatis.utils.BasePropertyUtil;
import com.lodsve.boot.example.dao.DemoDAO;
import com.lodsve.boot.example.pojo.form.DemoForm;
import com.lodsve.boot.example.pojo.po.DemoPO;
import com.lodsve.boot.example.pojo.query.DemoQuery;
import com.lodsve.boot.example.pojo.vo.DemoVO;
import com.lodsve.boot.example.service.DemoService;
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
public class DemoServiceImpl implements DemoService {
    private final DemoDAO demoDAO;

    public DemoServiceImpl(DemoDAO demoDAO) {
        this.demoDAO = demoDAO;
    }

    @Override
    public DemoVO loadDemo(Long id) {
        DemoPO demo = demoDAO.findById(id);
        if (null == demo) {
            return null;
        }
        return BeanMapper.map(demo, DemoVO.class);
    }

    @Override
    public boolean save(DemoForm demoForm) {
        if (null == demoForm) {
            return false;
        }
        if (demoForm.isCreate()) {
            // 创建
            DemoPO demo = new DemoPO();
            demo.setName(demoForm.getName());
            demo.setAge(demoForm.getAge());
            demo.setSex(demoForm.getSex());
            demo.setRemarks("create");

            BasePropertyUtil.initCreatedInfo(demo, 1L);
            demoDAO.save(demo);
            return true;
        } else {
            // 编辑
            DemoPO demo = demoDAO.findById(demoForm.getId());
            if (null == demo) {
                return false;
            }
            demo.setName(demoForm.getName());
            demo.setAge(demoForm.getAge());
            demo.setSex(demoForm.getSex());
            demo.setRemarks("update");

            BasePropertyUtil.initLastModifiedInfo(demo, 1L);
            demoDAO.updateAll(demo);
            return true;
        }
    }

    @Override
    public Page<DemoVO> findAll(Pageable pageable, DemoQuery query) {
        return demoDAO.findAll(pageable, query).map(d -> {
            DemoVO demo = new DemoVO();
            demo.setId(d.getId());
            demo.setName(d.getName());
            demo.setAge(d.getAge());
            demo.setSex(d.getSex());
            demo.setRemarks(d.getRemarks());
            return demo;
        });
    }

    @Override
    public boolean delete(Long id) {
        return demoDAO.deleteById(id);
    }

    @Override
    public boolean logicDelete(Long id) {
        return demoDAO.logicDeleteById(id);
    }
}
