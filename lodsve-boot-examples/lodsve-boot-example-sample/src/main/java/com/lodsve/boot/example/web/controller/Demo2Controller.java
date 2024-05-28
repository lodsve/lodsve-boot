package com.lodsve.boot.example.web.controller;

import com.lodsve.boot.component.webmvc.response.ResultWrapper;
import com.lodsve.boot.component.webmvc.response.SkipWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@RestController
@RequestMapping("/demo2")
public class Demo2Controller {
    @SkipWrapper
    @GetMapping("/t1")
    public String t1() {
        return "hello";
    }

    @ResultWrapper
    @GetMapping("/t2")
    public String t2() {
        return "hello";
    }
}
