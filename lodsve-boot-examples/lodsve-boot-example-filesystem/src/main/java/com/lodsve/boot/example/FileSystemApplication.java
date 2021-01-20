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

import com.lodsve.boot.filesystem.bean.Result;
import com.lodsve.boot.filesystem.enums.AccessControlEnum;
import com.lodsve.boot.filesystem.server.FileSystemServer;
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
    public Result testUploadFile() throws IOException {
        File f = new File(new ClassPathResource("META-INF/test.txt").getURI());

        return fileSystemServer.uploadFile(f, "test");
    }

    @GetMapping("/upload2")
    public Result testTestUploadFile() throws IOException {
        File f = new File(new ClassPathResource("META-INF/test.txt").getURI());

        return fileSystemServer.uploadFile(f, "test", AccessControlEnum.PUBLIC_READ);
    }

    @GetMapping("/url")
    public String testGetUrl(String name) {
        return fileSystemServer.getUrl("test/" + name);
    }

    @GetMapping("/url2")
    public String testTestGetUrl(String name) {
        return fileSystemServer.getUrl("test/" + name, 1000L);
    }

    @GetMapping("/download")
    public String testDownloadFileForStream(String name) throws IOException {
        return fileSystemServer.downloadFileForStream("test/" + name, "/Users/sunhao/Downloads/downloads");
    }

    @GetMapping("/delete")
    public void testDeleteFile(String name) {
        fileSystemServer.deleteFile("test/" + name);
    }

    @GetMapping("/exist")
    public boolean testIsExist(String name) {
        return fileSystemServer.isExist("test/" + name);
    }

}
