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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;

/**
 * 解压缩zip包.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class ZipUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * 私有化构造器
     */
    private ZipUtils() {
    }

    /**
     * 解压zip文件
     *
     * @param zipFilePath 需要解压的文件,绝对路径
     * @param zipPath     解压存储路径，相对路径
     * @return 解压zip文件
     * @throws Exception error
     */
    public static boolean unZip(String zipFilePath, String zipPath) throws Exception {
        if (StringUtils.isEmpty(zipFilePath) || StringUtils.isEmpty(zipPath)) {
            logger.error("this two params is requried!");
            return false;
        }

        //目标目录是否存在，存在不做任何动作，不存在则新建
        File destFile = new File(zipPath);
        if (!destFile.exists()) {
            FileUtils.createFolder(destFile);
        }

        //源zip文件
        File srcZipFile = new File(zipFilePath);
        if (!srcZipFile.getName().endsWith(".zip")) {
            logger.warn("this is not a zip file named '{}'", zipFilePath);
            return false;
        }

        //zip文件
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(srcZipFile);
        //zip文件包含的文件实体
        java.util.zip.ZipEntry entry;
        Enumeration<?> e = zipFile.entries();
        //遍历每一个zip包含的文件
        while (e.hasMoreElements()) {
            entry = (java.util.zip.ZipEntry) e.nextElement();
            if (entry.isDirectory()) {
                //是文件夹
                File file = new File(zipPath + entry.getName());
                file.mkdir();
            } else {
                //是文件
                InputStream is = zipFile.getInputStream(entry);
                File eFile = new File(zipPath + entry.getName());
                FileOutputStream fos = new FileOutputStream(eFile);
                int b;
                while ((b = is.read()) != -1) {
                    fos.write(b);
                }
                is.close();
                fos.close();
            }
        }
        zipFile.close();

        return true;
    }

    /**
     * 压缩
     *
     * @param zipFileName 打包后文件的名称,绝对路径
     * @param filePath    需要打包的文件夹或者文件的路径,绝对路径
     * @param pathName    打包到pathName文件夹下,文件夹名称
     * @throws Exception error
     */
    public static void zip(String zipFileName, String filePath, String pathName) throws Exception {
        if (StringUtils.isEmpty(zipFileName) || StringUtils.isEmpty(filePath)) {
            logger.error("this two params is requried!");
            return;
        }

        OutputStream out;
        BufferedOutputStream bos;
        ZipOutputStream zos;

        File f = new File(zipFileName);
        out = new FileOutputStream(f);
        bos = new BufferedOutputStream(out);
        zos = new ZipOutputStream(bos);
        zos.setEncoding("UTF-8");

        if (StringUtils.isNotBlank(pathName)) {
            pathName = pathName + File.separator;
        } else {
            pathName = f.getName().substring(0, f.getName().length() - 4);
        }

        doZip(zos, filePath, pathName);

        zos.flush();
        zos.close();
        bos.flush();
        bos.close();
        out.flush();
        out.close();
    }

    private static void doZip(ZipOutputStream zos, String filePath, String pathName) throws IOException {
        File file2zip = new File(filePath);
        if (file2zip.isFile()) {
            zos.putNextEntry(new org.apache.tools.zip.ZipEntry(pathName + file2zip.getName()));
            IOUtils.copy(new FileInputStream(file2zip.getAbsolutePath()), zos);
            zos.closeEntry();
        } else {
            File[] files = file2zip.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        doZip(zos, f.getAbsolutePath(), pathName + f.getName() + File.separator);
                    } else {
                        zos.putNextEntry(new org.apache.tools.zip.ZipEntry(pathName + File.separator + f.getName()));
                        IOUtils.copy(new FileInputStream(f.getAbsolutePath()), zos);
                        zos.closeEntry();
                    }
                }
            }
        }
    }
}
