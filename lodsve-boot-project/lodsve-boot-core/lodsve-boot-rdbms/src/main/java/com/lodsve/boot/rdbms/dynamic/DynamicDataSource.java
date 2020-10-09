package com.lodsve.boot.rdbms.dynamic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * 动态数据源.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2017/12/14 下午6:23
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private final Map<String, DataSource> dataSourceMap;
    private final String defaultDataSource;

    public DynamicDataSource(Map<String, DataSource> dataSourceMap, String defaultDataSource) {
        super.setDefaultTargetDataSource(defaultDataSource);
        this.dataSourceMap = dataSourceMap;
        this.defaultDataSource = defaultDataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getInstance().get();
    }

    @Override
    public void afterPropertiesSet() {
        if (MapUtils.isEmpty(dataSourceMap)) {
            throw new IllegalArgumentException("Property 'dataSourceMap' is required");
        }

        Map<Object, Object> targetDataSources = Maps.newHashMap();
        dataSourceMap.forEach(targetDataSources::put);

        super.setTargetDataSources(targetDataSources);

        if (StringUtils.isNotBlank(defaultDataSource)) {
            Set<String> keys = dataSourceMap.keySet();
            super.setDefaultTargetDataSource(Lists.newArrayList(keys).get(0));
        }

        super.afterPropertiesSet();
    }
}
