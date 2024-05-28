package com.lodsve.boot.example;

import com.lodsve.boot.component.countdown.CountdownPublisher;
import com.lodsve.boot.example.countdown.CountdownType;
import org.springframework.boot.SpringApplication;
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
public class CountdownApplication {
    private final CountdownPublisher publisher;

    public CountdownApplication(CountdownPublisher publisher) {
        this.publisher = publisher;
    }

    public static void main(String[] args) {
        SpringApplication.run(CountdownApplication.class);
    }

    @GetMapping("/index")
    public void index(String key, int expired) {
        publisher.countdown(key, expired, CountdownType.TEST);
    }
}
