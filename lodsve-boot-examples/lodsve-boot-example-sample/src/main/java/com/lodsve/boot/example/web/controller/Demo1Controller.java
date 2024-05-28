package com.lodsve.boot.example.web.controller;

import com.lodsve.boot.bean.WebResult;
import com.lodsve.boot.component.webmvc.response.ResultWrapper;
import com.lodsve.boot.component.webmvc.response.SkipWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/demo1")
public class Demo1Controller {
    @SkipWrapper
    @GetMapping("/t1")
    public String t1() {
        return "hello";
    }

    @GetMapping("/t2")
    public String t2() {
        return "hello";
    }

    @GetMapping("/t3")
    public WebResult<String> t3() {
        return WebResult.ok("hello");
    }

    @GetMapping("/t4")
    public ResponseEntity<String> t4() {
        return new ResponseEntity<>("hello", HttpStatus.OK);
    }
}
