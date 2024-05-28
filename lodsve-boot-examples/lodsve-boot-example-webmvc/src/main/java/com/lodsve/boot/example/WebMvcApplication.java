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
package com.lodsve.boot.example;

import com.lodsve.boot.LodsveBootApplication;
import com.lodsve.boot.component.webmvc.resolver.WebInput;
import com.lodsve.boot.component.webmvc.resolver.WebOutput;
import com.lodsve.boot.example.domain.User;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@SpringBootApplication
@RestController
public class WebMvcApplication {
    public static void main(String[] args) {
        LodsveBootApplication.run(WebMvcApplication.class, args);
    }

    @GetMapping("/input")
    public User input(WebInput in) {
        return new User("name", "telNO");
    }

    @GetMapping("/output")
    public void output(WebOutput out) throws Exception {
        out.toJson(new User("name", "telNO"));
    }
}
