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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class EncryptUtils {
    private static final Logger logger = LoggerFactory.getLogger(EncryptUtils.class);
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 用MD5算法进行加密
     *
     * @param plainText 明文，需要加密的字符串
     * @return MD5加密后的结果
     */
    public static String encodeMd5(String plainText) {
        return encode(plainText, EncryptType.MD5);
    }

    /**
     * 用SHA算法进行加密
     *
     * @param plainText 明文，需要加密的字符串
     * @return SHA加密后的结果
     */
    public static String encodeSha(String plainText) {
        return encode(plainText, EncryptType.SHA);
    }

    /**
     * 用base64算法进行加密
     *
     * @param plainText 明文，需要加密的字符串
     * @return base64加密后的结果
     */
    public static String encodeBase64(String plainText) {
        Assert.hasText(plainText, "plainText must not empty");

        return new String(Base64Utils.encode(plainText.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * 用base64算法进行解密
     *
     * @param cipherText 密文，需要解密的字符串
     * @return base64解密后的结果
     */
    public static String decodeBase64(String cipherText) {
        Assert.hasText(cipherText, "cipherText must not empty");

        return new String(Base64Utils.decode(cipherText.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * 根据类型加密
     *
     * @param plainText   明文
     * @param encryptType 加密类型
     * @return md5
     */
    private static String encode(String plainText, EncryptType encryptType) {
        String dstr = null;
        try {
            MessageDigest md = MessageDigest.getInstance(encryptType.name());
            md.update(plainText.getBytes());
            dstr = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return dstr;
    }

    /**
     * 获取文件MD5值
     *
     * @param file 文件
     * @return 文件MD5值
     */
    public static String getFileMd5(File file) {
        try {
            return getFileMd5(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    /**
     * 获取文件MD5值
     *
     * @param in 文件流
     * @return 文件流MD5值
     */
    public static String getFileMd5(FileInputStream in) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, in.available());
            messagedigest.update(byteBuffer);

            return bufferToHex(messagedigest.digest());
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 获取文件MD5值
     *
     * @param fileBytes 文件流
     * @return 文件流MD5值
     */
    public static String getFileMd5(byte[] fileBytes) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            messagedigest.update(fileBytes);
            return bufferToHex(messagedigest.digest());
        } catch (Exception e) {
            return "";
        }
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = HEX_DIGITS[(bt & 0xf0) >> 4];
        char c1 = HEX_DIGITS[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    /**
     * 加密类型
     */
    private enum EncryptType {
        /**
         * md5
         */
        MD5, SHA
    }
}
