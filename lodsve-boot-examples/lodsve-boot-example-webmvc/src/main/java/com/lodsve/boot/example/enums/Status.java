package com.lodsve.boot.example.enums;

import com.lodsve.boot.bean.Codeable;

/**
 * 用户状态枚举.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 15/8/10 上午11:45
 */
public enum Status implements Codeable {
    ENABLED("100", "启用"), DISABLED("101", "禁用"), AUDITING("102", "审核中"), DELETED("103", "已删除");

    private String code;
    private String title;

    Status(String code, String title) {
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
