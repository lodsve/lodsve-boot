package com.lodsve.boot.example.enums;

import com.lodsve.boot.bean.Codeable;
import com.lodsve.boot.bean.Description;

/**
 * 性别枚举.
 *
 * @author hulk
 */
@Description("性别")
public enum Gender implements Codeable {
    /**
     * 未填写
     */
    UNKNOWN("100", "未填写"),
    /**
     * 男
     */
    MALE("101", "男"),
    /**
     * 女
     */
    FEMALE("102", "女");

    private final String code;
    private final String title;

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
