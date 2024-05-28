package com.lodsve.boot.example.pojo.query;

import com.lodsve.boot.pojo.BaseQuery;
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
public class DemoQuery extends BaseQuery {
    private String name;
}
