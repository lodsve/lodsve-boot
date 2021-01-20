package com.lodsve.boot.example.web;

import com.google.common.collect.Lists;
import com.lodsve.boot.example.pojo.UserDTO;
import com.lodsve.boot.example.service.HomeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@RestController
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/default")
    public String switchNone() {
        return homeService.switchNone();
    }

    @GetMapping("/demo")
    public String switchDemo() {
        return homeService.switchDemo();
    }

    @GetMapping("/test")
    public String switchTest(String test) {
        return homeService.switchTest();
    }

    @GetMapping("/test2")
    public Page<UserDTO> test(Pageable pageable) {
        return new PageImpl<>(Lists.newArrayList(), pageable, 0);
    }
}
