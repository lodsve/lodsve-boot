package com.lodsve.boot.example.pojo.form;

import com.lodsve.boot.example.enums.Gender;
import com.lodsve.boot.pojo.BaseForm;
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
public class DemoForm extends BaseForm {
    private String name;
    private Long age;
    private Gender sex;
}
