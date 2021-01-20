package com.lodsve.boot.example.countdown;

import com.lodsve.boot.countdown.CountdownEventType;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/12 下午9:38
 */
public enum CountdownType implements CountdownEventType<CountdownType> {
    /**
     * test
     */
    TEST("test");

    String title;

    CountdownType(String title) {
        this.title = title;
    }

    @Override
    public String getType() {
        return title;
    }

    @Override
    public CountdownType eval(String type) {
        return TEST;
    }
}
