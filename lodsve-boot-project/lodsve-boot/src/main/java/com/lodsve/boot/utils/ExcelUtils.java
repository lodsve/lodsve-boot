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
package com.lodsve.boot.utils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入的工具类.
 *
 * @author Hulk Sun
 * @see CheckExcel
 */
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 私有化构造器
     */
    private ExcelUtils() {

    }

    /**
     * 获取excel文件中的数据对象
     *
     * @param excel            excel
     * @param excelColumnNames excel中每个字段的英文名(应该与pojo对象的字段名一致,顺序与excel一致)
     * @return excel每行是list一条记录，map是对应的"字段名--&gt;值"
     */
    public static List<Map<String, String>> evalData(InputStream excel, List<String> excelColumnNames) {
        Assert.notNull(excel, "execl不能为空！");
        Assert.notEmpty(excelColumnNames);

        Workbook workbook;
        try {
            //拿到excel
            workbook = Workbook.getWorkbook(excel);
        } catch (BiffException | IOException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
        logger.debug("workbook:{}", workbook);

        //第一个sheet
        Sheet sheet = workbook.getSheet(0);
        //行数
        int rowCounts = sheet.getRows() - 1;
        logger.debug("rowCounts:{}", rowCounts);
        List<Map<String, String>> list = new ArrayList<>(rowCounts - 1);

        //双重for循环取出数据
        for (int i = 1; i < rowCounts + 1; i++) {
            Map<String, String> params = new HashMap<>(excelColumnNames.size());
            //i,j i:行 j:列
            for (int j = 0; j < excelColumnNames.size(); j++) {
                Cell cell = sheet.getCell(j, i);
                params.put(excelColumnNames.get(j), cell.getContents());
            }

            list.add(params);
        }

        return list;
    }

    /**
     * 获取导入数据为对象的List
     *
     * @param is               excel
     * @param clazz            需要解析的bean类型
     * @param excelColumnNames execl字段名
     * @param checkExcel       校验数据
     * @param <T>              需要解析的bean类型
     * @return 解析的对象
     * @throws IllegalAccessException    clazz不可访问
     * @throws InstantiationException    clazz初始化异常
     * @throws InvocationTargetException 字段不存在
     */
    public static <T> List<T> evalBean(InputStream is, Class<T> clazz, List<String> excelColumnNames, CheckExcel checkExcel) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return evalBean(evalData(is, excelColumnNames), clazz, excelColumnNames, checkExcel);
    }

    /**
     * 获取导入数据为对象的List
     *
     * @param data             excel中的数据，即调用方法{{@link #evalData(InputStream, List)}}后得到的结果
     * @param clazz            需要解析的bean类型
     * @param excelColumnNames excel字段名
     * @param checkExcel       校验数据
     * @param <T>              需要解析的bean类型
     * @return 解析的对象
     * @throws IllegalAccessException    clazz不可访问
     * @throws InstantiationException    clazz初始化异常
     * @throws InvocationTargetException 字段不存在
     */
    public static <T> List<T> evalBean(List<Map<String, String>> data, Class<T> clazz, List<String> excelColumnNames, CheckExcel checkExcel) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Assert.notEmpty(data);
        Assert.notNull(clazz);
        Assert.notEmpty(excelColumnNames);

        List<T> result = new ArrayList<>(data.size());
        for (Map<String, String> d : data) {
            if (checkExcel != null && !checkExcel.check(d)) {
                continue;
            }
            T entity = clazz.newInstance();
            for (String column : excelColumnNames) {
                BeanUtils.setProperty(entity, column, d.get(column));
            }

            result.add(entity);
        }

        return result;
    }
}
