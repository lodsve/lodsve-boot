/*
 * Copyright © 2020 Sun.Hao(https://www.crazy-coder.cn/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.lodsve.boot.autoconfigure.mybatis;

import com.google.common.collect.Lists;
import com.lodsve.boot.mybatis.plugins.pagination.PaginationInterceptor;
import com.lodsve.boot.mybatis.plugins.repository.BaseRepositoryInterceptor;
import com.lodsve.boot.mybatis.repository.BaseRepository;
import com.lodsve.boot.mybatis.type.TypeHandlerScanner;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * mybatis configuration.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ConditionalOnClass({BaseRepository.class, SqlSessionFactory.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(MybatisProperties.class)
@org.springframework.context.annotation.Configuration
public class MybatisAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    private final Interceptor[] interceptors;
    private final MybatisProperties mybatisProperties;

    public MybatisAutoConfiguration(ObjectProvider<Interceptor[]> interceptors, ObjectProvider<MybatisProperties> mybatisProperties) {
        this.interceptors = interceptors.getIfAvailable();
        this.mybatisProperties = mybatisProperties.getIfUnique();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(ObjectProvider<DataSource> dataSource, ObjectProvider<List<ConfigurationCustomizer>> customizers) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource.getIfUnique());
        if (ArrayUtils.isNotEmpty(interceptors)) {
            factory.setPlugins(interceptors);
        }

        Configuration configuration = new Configuration();
        customizers.getIfAvailable(Lists::newArrayList).forEach(c -> c.customize(configuration));
        // 默认的配置
        defaultCustomizer().customize(configuration);
        factory.setConfiguration(configuration);

        return factory.getObject();
    }

    private ConfigurationCustomizer defaultCustomizer() {
        return c -> {
            c.setMapUnderscoreToCamelCase(mybatisProperties.isMapUnderscoreToCamelCase());

            c.addInterceptor(new PaginationInterceptor());
            c.addInterceptor(new BaseRepositoryInterceptor());

            if (ArrayUtils.isNotEmpty(mybatisProperties.getEnumsLocations())) {
                TypeHandler<?>[] handlers = new TypeHandlerScanner().find(mybatisProperties.getEnumsLocations());
                Arrays.stream(handlers).forEach(c.getTypeHandlerRegistry()::register);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @org.springframework.context.annotation.Configuration
    @Import(MybatisMapperScannerImportBeanDefinitionRegistrar.class)
    @ConditionalOnMissingBean({MapperFactoryBean.class, MapperScannerConfigurer.class})
    public static class MybatisMapperScannerAutoConfigure implements InitializingBean {
        @Override
        public void afterPropertiesSet() throws Exception {
            logger.debug("Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
        }
    }

    public static class MybatisMapperScannerImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
        private BeanFactory beanFactory;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
                return;
            }

            logger.debug("Searching for mappers annotated with @Mapper");

            List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
            if (logger.isDebugEnabled()) {
                packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
            }

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
            builder.addPropertyValue("processPropertyPlaceHolders", true);
            builder.addPropertyValue("annotationClass", Repository.class);
            builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
            registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }
}
