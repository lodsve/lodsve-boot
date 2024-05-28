package com.lodsve.boot.example.pojo.vo;

import com.lodsve.boot.example.enums.Gender;
import com.lodsve.boot.pojo.BaseVO;
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
public class TestVO extends BaseVO {
    private Long id;
    private String name;
    private Long age;
    private Gender sex;
    private String remarks;
}
