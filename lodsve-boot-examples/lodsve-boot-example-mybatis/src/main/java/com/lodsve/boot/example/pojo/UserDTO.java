package com.lodsve.boot.example.pojo;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserDTO {
    private String id;
    private String name;
}
