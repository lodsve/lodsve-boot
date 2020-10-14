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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * swagger配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ConfigurationProperties(prefix = "lodsve.swagger")
@Data
public class SwaggerProperties {
    /**
     * 接口版本号
     */
    private String version;
    /**
     * 接口文档标题
     */
    private String title;
    /**
     * 接口文档描述
     */
    private String description;
    /**
     * 项目团队地址
     */
    private String termsOfServiceUrl;
    /**
     * 项目使用的许可证
     */
    private String license;
    /**
     * 许可证描述地址
     */
    private String licenseUrl;
    /**
     * 项目联系人
     */
    private Contact contact;

    @Data
    public static class Contact {
        /**
         * 项目联系人
         */
        private String name;
        /**
         * 项目联系人主页
         */
        private String url;
        /**
         * 项目联系人邮箱
         */
        private String email;
    }
}
