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
package com.lodsve.boot.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 编码URI组件，与JavaScript的encodeURIComponent函数一致.
 *
 * @author Hulk Sun
 */
public final class UrlEncoderUtil {
    /**
     * 编码URI组件，与JavaScript的encodeURIComponent函数一致.
     *
     * @param input 输入字符串
     * @return 编码后的字符串
     */
    public static String encodeUriComponent(String input) {
        // 使用 UTF-8 编码，并将空格替换为 %20（而不是 +）
        return URLEncoder.encode(input, StandardCharsets.UTF_8)
            .replace("+", "%20")
            // 保留 ! 不编码
            .replace("%21", "!")
            // 保留 ' 不编码
            .replace("%27", "'")
            // 保留 ( 不编码
            .replace("%28", "(")
            // 保留 ) 不编码
            .replace("%29", ")")
            // 保留 * 不编码
            .replace("%2A", "*");
    }

    /**
     * 解码URI组件，与JavaScript的decodeURIComponent函数一致.
     *
     * @param input 输入字符串
     * @return 解码后的字符串
     */
    public static String decodeUriComponent(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return URLDecoder.decode(input, StandardCharsets.UTF_8);
    }
}
