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
package com.lodsve.boot.filesystem.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 上传图片返回DTO.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@Data
@ToString
public class Result implements Serializable {
    /**
     * 文件唯一标识
     */
    private String objectName;
    /**
     * Object生成时会创建相应的ETag (entity tag) ，ETag用于标示一个Object的内容。
     * 1. 对于PutObject请求创建的Object，ETag值是其内容的MD5值。
     * 2. 对于其他方式创建的Object，ETag值是其内容的UUID。
     */
    private String etag;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件md5值
     */
    private String md5;
    /**
     * 上传结果
     */
    private Boolean result;
}
