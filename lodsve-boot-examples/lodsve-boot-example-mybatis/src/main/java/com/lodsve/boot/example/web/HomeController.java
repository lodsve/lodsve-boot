/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
