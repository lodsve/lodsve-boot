package com.lodsve.boot.example.countdown;

import com.lodsve.boot.component.countdown.CountdownEventHandler;
import com.lodsve.boot.component.countdown.CountdownEventType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/12 下午9:42
 */
@Component
public class CountdownTestHandler implements CountdownEventHandler {

    @Override
    public void handler(Serializable key) {
        System.out.println(getEventType().getType() + "===========" + key);
    }

    @Override
    public Serializable resolveKey(String message) {
        return message;
    }

    @Override
    public CountdownEventType<CountdownType> getEventType() {
        return CountdownType.TEST;
    }
}
