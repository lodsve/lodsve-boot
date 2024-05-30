/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
