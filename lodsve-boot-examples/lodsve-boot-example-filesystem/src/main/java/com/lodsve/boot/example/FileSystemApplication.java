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
