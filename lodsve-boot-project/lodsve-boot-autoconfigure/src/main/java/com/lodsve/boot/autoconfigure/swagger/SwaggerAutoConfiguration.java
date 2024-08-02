/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lodsve.boot.autoconfigure.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.lodsve.boot.autoconfigure.swagger.SwaggerAutoConfiguration.IgnoreAccountParamInSwaggerConfiguration;
import com.lodsve.boot.autoconfigure.swagger.SwaggerProperties.AuthConfig;
import com.lodsve.boot.autoconfigure.swagger.SwaggerProperties.GlobalParameter;
import com.lodsve.boot.component.security.core.Account;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * springfox配置.
 *
 * @author Hulk Sun
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "lodsve.swagger.enabled")
@ConditionalOnClass(Docket.class)
@EnableConfigurationProperties(SwaggerProperties.class)
@Import({Swagger2DocumentationConfiguration.class, IgnoreAccountParamInSwaggerConfiguration.class, SwaggerAutoConfiguration.CompatibleSpringBootAndSwagger.class})
public class SwaggerAutoConfiguration implements BeanFactoryAware {
    private static final String PREFERRED_MAPPER_PROPERTY = "spring.mvc.converters.preferred-json-mapper";
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
            .apiInfo(apiInfo).select();

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

        Docket docket = builder.apis(predicate).paths(PathSelectors.any()).build();
        List<GlobalParameter> globalParameters = swaggerProperties.getGlobalParameters();
        if (CollectionUtils.isNotEmpty(globalParameters)) {
            List<RequestParameter> parameters = globalParameters.stream().map(gb -> new RequestParameterBuilder()
                .name(gb.getName())
                .description(gb.getDescription())
                .required(gb.isRequired())
                .in(ParameterType.from(gb.getScope()))
                .query(q -> q.model(m -> m.scalarModel(ScalarType.from(gb.getType(), "").orElse(ScalarType.STRING))))
                .build()).collect(Collectors.toList());
            docket.globalRequestParameters(parameters);
        }
        AuthConfig authConfig = swaggerProperties.getAuth();
        if (null != authConfig && authConfig.isEnabled() && StringUtils.isNoneBlank(authConfig.getKey())) {
            List<SecurityScheme> securitySchemes = Lists.newArrayList(new ApiKey(authConfig.getKey(), authConfig.getKey(), "header"));
            docket.securitySchemes(securitySchemes);

            AuthorizationScope authorizationScope = new AuthorizationScope("global", "authorization");
            List<SecurityReference> securityReferences = Lists.newArrayList(new SecurityReference(authConfig.getKey(), new AuthorizationScope[]{authorizationScope}));
            List<SecurityContext> securityContexts = Lists.newArrayList(SecurityContext.builder().securityReferences(securityReferences).forPaths(PathSelectors.any()).build());
            docket.securityContexts(securityContexts);
        }
        return docket;
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
    @ConditionalOnMissingBean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder().build();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @ConditionalOnClass({Pageable.class, Docket.class})
    @Configuration
    public static class SpringDataPageableSupportConfiguration {
        @Bean
        public AlternateTypeRuleConvention pageableConvention(ObjectProvider<TypeResolver> objectProvider) {
            TypeResolver resolver = (null == objectProvider.getIfUnique() ? new TypeResolver() : objectProvider.getIfUnique());
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

    @Configuration
    @ConditionalOnClass(Gson.class)
    @AutoConfigureAfter({SwaggerAutoConfiguration.class, GsonAutoConfiguration.class})
    @ConditionalOnProperty(name = PREFERRED_MAPPER_PROPERTY, havingValue = "gson")
    public static class GsonRedisSerializerConfiguration {
        @Bean
        public GsonBuilderCustomizer customizer() {
            return builder -> {
                builder.registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter());
            };
        }

        private static class SpringfoxJsonToGsonAdapter implements JsonSerializer<Json> {
            @Override
            public JsonElement serialize(Json json, Type typeOfSrc, JsonSerializationContext context) {
                return JsonParser.parseString(json.value());
            }
        }
    }

    @Configuration
    @ConditionalOnClass(Account.class)
    public static class IgnoreAccountParamInSwaggerConfiguration implements InitializingBean {
        private final Docket docket;

        public IgnoreAccountParamInSwaggerConfiguration(Docket docket) {
            this.docket = docket;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            docket.ignoredParameterTypes(Account.class);
        }
    }

    /**
     * 兼容spring boot2.6.3与springfox
     * springboot2.6.x以及上版本默认使用的PATH_PATTERN_PARSER而knife4j的springfox使用的是ANT_PATH_MATCHER导致的，springboot的yml文件配置url匹配规则
     */
    @Configuration
    public static class CompatibleSpringBootAndSwagger {
        @Bean
        public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
            return new BeanPostProcessor() {

                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                    if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                        customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                    }
                    return bean;
                }

                private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                    List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                    mappings.clear();
                    mappings.addAll(copy);
                }

                @SuppressWarnings("unchecked")
                private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                    try {
                        Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                        field.setAccessible(true);
                        return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            };
        }
    }

}
