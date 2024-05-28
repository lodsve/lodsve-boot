package com.lodsve.boot.example.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * .
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/18 下午6:41
 */
@Data
public class User implements Serializable {
    private Long id;
    private String userName;
    private String telNo;
    private Date createAt;
}
