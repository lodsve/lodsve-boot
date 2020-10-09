package com.lodsve.boot.autoconfigure.rdbms;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 数据源的配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ConfigurationProperties(prefix = "lodsve.rdbms")
@Data
public class RdbmsProperties {
    /**
     * 默认数据源名称，不填为{@link #dataSourceProperties}的第一个值
     */
    private String defaultDataSourceName;
    /**
     * 多数据源配置
     * 数据源名称 -> 数据源配置
     */
    private Map<String, DataSourceProperties> dataSourceProperties;
}
