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

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取汉字拼音的工具类
 *
 * @author Hulk Sun
 */
public class PinyinUtils {

    /**
     * 私有化构造器
     */
    private PinyinUtils() {
    }

    public static String getPinyin(String src) throws Exception {
        return generate(getPinyinList(src, null, false, false));
    }

    public static String getSinglePinyin(String src) throws Exception {
        return generate(getPinyinList(src, null, false, true));
    }

    public static String getPinyin(String src, String separator) throws Exception {
        return generate(getPinyinList(src, separator, false, false));
    }

    public static String getSinglePinyin(String src, String separator) throws Exception {
        return generate(getPinyinList(src, separator, false, true));
    }

    public static String getShortPinyin(String src) throws Exception {
        return generate(getPinyinList(src, null, true, false));
    }

    public static String getSingleShortPinyin(String src) throws Exception {
        if (StringUtils.isBlank(src)) {
            return src;
        }
        return generate(getPinyinList(src, null, true, true));
    }

    /**
     * 字符串集合转换字符串(逗号分隔)
     *
     * @param stringSet
     * @return
     */
    private static String generate(List<StringBuffer> stringSet) {
        if (stringSet == null || stringSet.isEmpty()) {
            return StringUtils.EMPTY;
        }

        StringBuffer resultString = new StringBuffer();

        for (int i = 0; i < stringSet.size(); i++) {
            StringBuffer res = stringSet.get(i);
            if (i != 0) {
                resultString.append(',');
            }
            resultString.append(res);
        }
        return resultString.toString();
    }

    /**
     * 获取汉字的拼音
     *
     * @param src            汉字
     * @param separator      分隔符
     * @param getFirstLetter 是否获取单个字拼音的首字母
     * @param isSingle       是否获取多音字的所有拼音
     * @return 汉字的拼音
     * @throws BadHanyuPinyinOutputFormatCombination error pingyin
     */
    public static List<StringBuffer> getPinyinList(String src, String separator, boolean getFirstLetter, boolean isSingle) throws BadHanyuPinyinOutputFormatCombination {
        if (StringUtils.isNotEmpty(src)) {
            char[] srcChar = src.toCharArray();
            //汉语拼音格式输出类
            HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();

            //输出设置，大小写，音标方式等
            hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            String[][] temp = new String[(src.length())][];
            for (int i = 0; i < srcChar.length; i++) {
                char c = srcChar[i];
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] parray = PinyinHelper.toHanyuPinyinStringArray(c, hanyuPinyinOutputFormat);
                    //如果不用多音字
                    if (isSingle && parray.length > 0) {
                        parray = new String[]{parray[0]};
                    } else {
                        //去除重复的
                        parray = removeRepeat(parray);
                    }
                    if (getFirstLetter) {
                        temp[i] = getFristLetter(parray);
                    } else {
                        temp[i] = parray;
                    }
                } else {
                    temp[i] = new String[]{String.valueOf(c)};
                }
            }

            int position = 0;
            List<StringBuffer> resultList = new ArrayList<>();
            while (position < temp.length) {
                position = remakeResultList(temp, position, resultList, separator);
            }

            return resultList;
        }

        return null;
    }

    /**
     * 获取拼音的首字母
     *
     * @param wholePinyin 拼音数组
     * @return
     */
    private static String[] getFristLetter(String[] wholePinyin) {
        for (int i = 0; i < wholePinyin.length; i++) {
            wholePinyin[i] = wholePinyin[i].substring(0, 1);
        }
        return removeRepeat(wholePinyin);
    }

    /**
     * 去除重复的项
     *
     * @param parray 源数组
     * @return
     */
    private static String[] removeRepeat(String[] parray) {
        Set<String> set = new HashSet<>();
        for (String py : parray) {
            set.add(py);
        }
        return set.toArray(new String[set.size()]);
    }

    /**
     * 对多音字的拼音数组进行处理
     *
     * @param temp
     * @param position
     * @param resultList
     * @param separator
     * @return
     */
    private static int remakeResultList(String[][] temp, int position, List<StringBuffer> resultList, String separator) {
        if (position == 0) {
            for (String py : temp[0]) {
                resultList.add(new StringBuffer(py));
            }
        } else {
            int oldSize = resultList.size();
            for (int i = 0; i < oldSize; i++) {
                StringBuffer oldPy = resultList.get(0);
                String[] pyList2 = temp[position];
                for (int j = 0, jsize = pyList2.length; j < jsize; j++) {
                    StringBuffer coppiedBuffer = new StringBuffer(oldPy);
                    if (separator == null) {
                        resultList.add(coppiedBuffer.append(pyList2[j]));
                    } else {
                        resultList.add(coppiedBuffer.append(separator).append(pyList2[j]));
                    }
                }
                resultList.remove(0);
            }
        }
        return position + 1;
    }

}
