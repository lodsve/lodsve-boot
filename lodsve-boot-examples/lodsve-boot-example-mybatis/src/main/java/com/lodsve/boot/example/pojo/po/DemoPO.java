package com.lodsve.boot.example.pojo.po;

import com.lodsve.boot.component.mybatis.pojo.BasePropertyPO;
import com.lodsve.boot.example.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;

/**
 * .
 *
 * @author Hulk Sun
 */
@Setter
@Getter
@ToString(callSuper = true)
@Table(name = "t_demo")
public class DemoPO extends BasePropertyPO {
    private String name;
    private Long age;
    private Gender sex;
}
