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
package com.lodsve.boot.autoconfigure.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * springfox配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Configuration
@ConditionalOnWebApplication
@EnableSwagger2
@ConditionalOnMissingBean(Docket.class)
@ConditionalOnProperty(name = "lodsve.swagger.enabled")
@ConditionalOnClass(Docket.class)
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration implements BeanFactoryAware {
    private final SwaggerProperties swaggerProperties;
    private BeanFactory beanFactory;

    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public SwaggerUiWebMvcConfigurer swaggerUiConfigurer() {
        return new SwaggerUiWebMvcConfigurer();
    }

    @Bean
    @ConditionalOnMissingBean(Docket.class)
    public Docket defaultDocket(ApiInfo apiInfo) {
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo)
            .ignoredParameterTypes(Pageable.class).select();

        List<String> basePackages = AutoConfigurationPackages.get(beanFactory);
        if (CollectionUtils.isEmpty(basePackages)) {
            throw new RuntimeException("can not find any base packages!");
        }
        Predicate<RequestHandler> predicate;
        if (1 == basePackages.size()) {
            predicate = RequestHandlerSelectors.basePackage(basePackages.get(0));
        } else {
            List<Predicate<RequestHandler>> predicates = Lists.newArrayList();
            basePackages.forEach(bp -> predicates.add(RequestHandlerSelectors.basePackage(bp)));
            predicate = predicates.stream().reduce(x -> true, Predicate::or);
        }

        return builder.apis(predicate).paths(PathSelectors.any()).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiInfo apiInfo() {
        SwaggerProperties.Contact contactProp = swaggerProperties.getContact();
        Contact contact = null;
        if (null != contactProp) {
            contact = new Contact(contactProp.getName(), contactProp.getUrl(), contactProp.getEmail());
        }

        return new ApiInfo(swaggerProperties.getTitle(),
            swaggerProperties.getDescription(),
            swaggerProperties.getVersion(),
            swaggerProperties.getTermsOfServiceUrl(),
            contact,
            swaggerProperties.getLicense(),
            swaggerProperties.getLicenseUrl(),
            Lists.newArrayList());
    }

    @Bean
    @ConditionalOnClass(Pageable.class)
    public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return singletonList(newRule(resolver.resolve(Pageable.class), resolver.resolve(SwaggerPageable.class)));
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder().build();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @ApiModel
    static class SwaggerPageable {
        @ApiModelProperty(value = "当前页码", example = "0")
        private Integer page;

        @ApiModelProperty(value = "每页记录数", example = "10")
        private Integer size;

        @ApiModelProperty(value = "排序,格式{字段名,ASC|DESC},可以多条记录")
        private List<String> sort;

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public List<String> getSort() {
            return sort;
        }

        public void setSort(List<String> sort) {
            this.sort = sort;
        }
    }
}
