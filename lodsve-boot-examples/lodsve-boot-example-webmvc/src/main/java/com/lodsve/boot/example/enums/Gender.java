package com.lodsve.boot.example.enums;


import com.lodsve.boot.bean.Codeable;

/**
 * 性别枚举.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 15/8/10 上午11:45
 */
public enum Gender implements Codeable {
    UNKNOWN("100", "未填写"), MALE("101", "男"), FEMALE("102", "女");

    private String code;
    private String title;

    Gender(String code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
