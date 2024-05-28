package com.lodsve.boot.example.web.controller;

import com.lodsve.boot.component.webmvc.response.ResultWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ResultWrapper
@RestController
@RequestMapping("/demo3")
public class Demo3Controller {
    @GetMapping("/t1")
    public Integer t1() {
        return 1;
    }

    @GetMapping("/t2")
    public Boolean t2() {
        return true;
    }

    @GetMapping("/t3")
    public Long t3() {
        return 1L;
    }

    @GetMapping("/t4")
    public Float t4() {
        return 1F;
    }

    @GetMapping("/t5")
    public Double t5() {
        return 1D;
    }

    @GetMapping("/t6")
    public int t6() {
        return 1;
    }
}
