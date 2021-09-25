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

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间的工具类
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @see org.apache.commons.lang3.time.DateUtils
 */
public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * Default date format pattern.
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 私有化构造器
     */
    private DateUtils() {
    }

    /**
     * 把字符串转成日期型
     *
     * @param date    日期字符串
     * @param pattern 表达式
     * @return 日期型
     */
    public static Date parseDate(final String date, final String pattern) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        String pat = pattern;
        if (StringUtils.isEmpty(pat)) {
            pat = DEFAULT_PATTERN;
        }
        try {
            return new SimpleDateFormat(pat).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将日期型转成字符串型
     *
     * @param date    日期
     * @param pattern 表达式
     * @return 字符串日期
     */
    public static String formatDate(Date date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = DEFAULT_PATTERN;
        }

        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 获取给定时间的那天的最后时刻
     *
     * @param day 给定时间(em.2011-01-25 22:11:00...)
     * @return 给定时间的那天的最后时刻(em.2011 - 01 - 25 23 : 59 : 59...)
     */
    public static Date getEndOfDay(Date day) {
        if (day == null) {
            day = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 获取给定时间的那天的开始时刻
     *
     * @param day 给定时间(em.2011-01-25 22:11:00...)
     * @return 给定时间的那天的最后时刻(em.2011 - 01 - 25 00 : 00 : 00...)
     */
    public static Date getStartOfDay(Date day) {
        if (day == null) {
            day = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));

        return calendar.getTime();
    }

    /**
     * 获取给定时间的那个月的最后时刻
     *
     * @param day 给定时间(em.2011-01-25 22:11:00...)
     * @return 给定时间的那个月的最后时刻(em.2011 - 01 - 31 23 : 59 : 59...)
     */
    public static Date getEndOfMonth(Date day) {
        if (day == null) {
            day = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);

        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        calendar.add(Calendar.MONTH, 1);

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return calendar.getTime();
    }

    /**
     * 获取给定时间的那个月的开始时刻
     *
     * @param day 给定时间(em.2011-01-25 22:11:00...)
     * @return 给定时间的那个月的开始时刻(em.2011 - 01 - 01 00 : 00 : 00...)
     */
    public static Date getStartOfMonth(Date day) {
        if (day == null) {
            day = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);

        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    /**
     * 获取给定时间的那天的正午时刻
     *
     * @param day 给定时间(em.2011-01-25 22:11:00...)
     * @return 给定时间的那天的最后时刻(em.2011 - 01 - 25 12 : 00 : 00...)
     */
    public static Date getNoonOfDay(Date day) {
        if (day == null) {
            day = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static Date getNow() {
        return new Date();
    }

    /**
     * 获取给定日期的星期数
     *
     * @param prefix 前缀.eg.'星期'
     * @param date   给定日期 eg.'2012-01-25 23:07:58'
     * @return 前缀+(星期数) eg.'星期三'
     */
    public static String getDayOfWeek(String prefix, Date date) {
        final String[] dayNames = {"日", "一", "二", "三", "四", "五", "六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) {
            dayOfWeek = 0;
        }

        return prefix + dayNames[dayOfWeek];
    }

    /**
     * 获取给定日期的星期数(默认前缀:'星期')
     *
     * @param date 给定日期
     * @return 星期数
     */
    public static String getDayOfWeek(Date date) {
        return getDayOfWeek("星期", date);
    }

    /**
     * 获取当前日历所属的年.
     *
     * @return 当前日历所属的年.
     */
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取当前日历所属的月，月份是从1开始的.
     *
     * @return 当前日历所属的月.
     */
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取间隔给定天数的日期 <br>
     *
     * @param date 给定的日期(eg:2015-06-02 12:00)
     * @param days 间隔的天数，正数是给定时间往后，负数是给定时间往前(eg:-1 or 1)
     * @return 计算出来的日期(eg : 2015 - 06 - 01 12 : 00 or 2015 - 06 - 03 12 : 00)<br>
     */
    public static Date intervalSomeDays(Date date, int days) {
        if (date == null) {
            return date;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + days);

        return cal.getTime();
    }

    /**
     * 获取间隔给定月数的日期 <br>
     *
     * @param date   给定的日期(eg:2015-06-02 12:00)
     * @param months 间隔的月数，正数是给定时间往后，负数是给定时间往前(eg:-1 or 1)
     * @return 计算出来的日期(eg : 2015 - 05 - 02 12 : 00 or 2015 - 07 - 02 12 : 00)<br>
     */
    public static Date intervalSomeMonths(Date date, int months) {
        if (date == null) {
            return date;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + months);

        return cal.getTime();
    }

    /**
     * 获取间隔给定月数的日期 <br>
     *
     * @param date  给定的日期(eg:2015-06-02 12:00)
     * @param years 间隔的月数，正数是给定时间往后，负数是给定时间往前(eg:-1 or 1)
     * @return 计算出来的日期(eg : 2014 - 06 - 02 12 : 00 or 2016 - 06 - 02 12 : 00)<br>
     */
    public static Date intervalSomeYears(Date date, int years) {
        if (date == null) {
            return date;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + years);

        return cal.getTime();
    }
}
