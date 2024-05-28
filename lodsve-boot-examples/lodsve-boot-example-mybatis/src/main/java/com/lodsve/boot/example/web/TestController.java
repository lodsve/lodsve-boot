package com.lodsve.boot.example.web;

import com.lodsve.boot.example.pojo.form.TestForm;
import com.lodsve.boot.example.pojo.query.TestQuery;
import com.lodsve.boot.example.pojo.vo.TestVO;
import com.lodsve.boot.example.service.TestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author Hulk Sun
 */
@RestController
@RequestMapping("/test")
public class TestController {
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/{id}")
    public TestVO loadUser(@PathVariable("id") Long id) {
        return testService.loadTest(id);
    }

    @PatchMapping("/")
    public boolean update(@RequestBody TestForm testForm) {
        return testService.save(testForm);
    }

    @PostMapping("/")
    public boolean save(@RequestBody TestForm testForm) {
        return testService.save(testForm);
    }

    @PostMapping("/list")
    public Page<TestVO> findAll(@PageableDefault Pageable pageable, @RequestBody TestQuery query) {
        return testService.findAll(pageable, query);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return testService.delete(id);
    }

    @DeleteMapping("/logic-delete/{id}")
    public boolean logicDelete(@PathVariable("id") Long id) {
        return testService.logicDelete(id);
    }
}
