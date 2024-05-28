package com.lodsve.boot.example.pojo.bo;

import com.lodsve.boot.example.enums.Gender;
import com.lodsve.boot.pojo.BaseBO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * .
 *
 * @author Hulk Sun
 */
@Setter
@Getter
@ToString(callSuper = true)
public class TestBO extends BaseBO {
    private Long id;
    private String name;
    private Long age;
    private Gender sex;
    private String remarks;
}
