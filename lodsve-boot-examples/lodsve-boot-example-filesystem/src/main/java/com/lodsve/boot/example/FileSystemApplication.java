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

import com.lodsve.boot.component.filesystem.bean.FileSystemResult;
import com.lodsve.boot.component.filesystem.enums.AccessControlEnum;
import com.lodsve.boot.component.filesystem.server.FileSystemServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@SpringBootApplication
@RestController
public class FileSystemApplication {
    private final FileSystemServer fileSystemServer;

    public FileSystemApplication(FileSystemServer fileSystemServer) {
        this.fileSystemServer = fileSystemServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileSystemApplication.class, args);
    }

    @GetMapping("/upload")
    public FileSystemResult testUploadFile() throws IOException {
        File f = new File(new ClassPathResource("META-INF/test.txt").getURI());

        return fileSystemServer.uploadFile("test", f, "test");
    }

    @GetMapping("/upload2")
    public FileSystemResult testTestUploadFile() throws IOException {
        File f = new File(new ClassPathResource("META-INF/test.txt").getURI());

        return fileSystemServer.uploadFile("test", f, "test", AccessControlEnum.PUBLIC_READ);
    }

    @GetMapping("/url")
    public String testGetUrl(String name) {
        return fileSystemServer.getUrl("test", "test/" + name);
    }

    @GetMapping("/url2")
    public String testTestGetUrl(String name) {
        return fileSystemServer.getUrl("test", "test/" + name, 1000L);
    }

    @GetMapping("/download")
    public String testDownloadFileForStream(String name) throws IOException {
        return fileSystemServer.downloadFileForStream("test", "test/" + name, "/Users/sunhao/Downloads/downloads");
    }

    @GetMapping("/delete")
    public void testDeleteFile(String name) {
        fileSystemServer.deleteFile("test", "test/" + name);
    }

    @GetMapping("/exist")
    public boolean testIsExist(String name) {
        return fileSystemServer.isExist("test", "test/" + name);
    }

}
