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
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@SpringBootApplication
public class MybatisApplication {
    public static void main(String[] args) {
        LodsveBootApplication.run(MybatisApplication.class, args);
    }

    private static void encrypt() {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("your password");
        //要加密的数据（数据库的用户名或密码）
        String text = textEncryptor.encrypt("the text need encrypt!");
        System.out.println("text: " + text);
    }
}
