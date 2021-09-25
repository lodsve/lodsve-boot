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
package com.lodsve.boot.component.webmvc.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * HttpServletRequest的工具类
 *
 * @author Hulk Sun
 */
public class RequestUtils {
    /**
     * request中contextPath
     */
    public static final String DEFAULT_CONTEXT_PATH = "contextPath";

    /**
     * 私有化构造器
     */
    private RequestUtils() {
    }

    /**
     * 判断是否是AJAX请求
     *
     * @param request request
     * @return 是否是AJAX请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return StringUtils.isNotEmpty(header) && "XMLHttpRequest".equals(header);
    }

    /**
     * 获取此次请求的url(不带contextPath)
     *
     * @param request    此次请求
     * @param needParams 是否需要参数
     * @return URL(未encoding)
     */
    public static String getRequestUrl(HttpServletRequest request, boolean needParams) {
        StringBuilder requestUrl = new StringBuilder(request.getServletPath());
        if (!needParams) {
            return requestUrl.toString();
        }

        //所带参数的MAP
        Map params = request.getParameterMap();
        Iterator it = params.keySet().iterator();
        if (it.hasNext()) {
            requestUrl.append("?");
        }
        while (it.hasNext()) {
            String key = (String) it.next();
            String[] values = (String[]) params.get(key);
            String value;
            if (values != null && values.length > 0) {
                value = values[0];
            } else {
                value = StringUtils.EMPTY;
            }

            if (StringUtils.isNotEmpty(key)) {
                //key必须不为空，value可以为空
                requestUrl.append(key).append("=").append(value).append("&");
            }
        }

        if (requestUrl.toString().endsWith("&")) {
            return requestUrl.substring(0, requestUrl.length() - 1);
        }

        return requestUrl.toString();
    }

    /**
     * 获得请求中所带的参数
     *
     * @param request request
     * @return 请求中所带的参数
     */
    public static List<String> getRequestParam(HttpServletRequest request) {
        List<String> urlParams = new ArrayList<>();
        Map params = request.getParameterMap();
        Iterator it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String[] values = (String[]) params.get(key);
            if (values != null && values.length > 0) {
                urlParams.add(values[0]);
            }
        }

        return urlParams;
    }

    /**
     * 获取contextPath
     *
     * @param request request
     * @return contextPath
     */
    public static String getContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute(DEFAULT_CONTEXT_PATH);
        if (StringUtils.isEmpty(contextPath)) {
            contextPath = request.getContextPath();

            if ((contextPath != null) && (contextPath.length() > 0)) {
                if ("/".equals(StringUtils.right(contextPath, 1))) {
                    contextPath = StringUtils.substringBeforeLast(contextPath, "/");
                }
            } else {
                contextPath = "";
            }

            request.setAttribute(DEFAULT_CONTEXT_PATH, contextPath);
        }

        return contextPath;
    }

    /**
     * get referer page from request object
     *
     * @param request HttpServletRequest object.
     * @return url of referer page.
     */
    public static String getReferer(final HttpServletRequest request) {
        String url = request.getParameter("referer");
        if (StringUtils.isBlank(url)) {
            url = request.getHeader("referer");
        }
        return url;
    }

    /**
     * 将一个对象同步到session中
     *
     * @param request request
     * @param key     key
     * @param object  对象
     */
    public static void syncToSession(HttpServletRequest request, String key, Object object) {
        HttpSession session = request.getSession();
        session.setAttribute(key, object);
    }

    /**
     * 将一个对象同步到session中
     *
     * @param request request
     * @param key     key
     * @param object  对象
     * @param life    存活时间
     */
    public static void syncToSession(HttpServletRequest request, String key, Object object, Integer life) {
        HttpSession session = request.getSession();
        session.setAttribute(key, object);
        session.setMaxInactiveInterval(life);
    }

    /**
     * 给定一段URL后面的参数,返回参数的map集合
     *
     * @param params 参数
     * @return 参数的map集合
     */
    public static Map<String, Object> getParams(String params) {
        if (StringUtils.isEmpty(params)) {
            return Collections.emptyMap();
        }

        String[] ps = params.split("&");
        Map<String, Object> paramsMap = new HashMap<>(ps.length);
        for (String p : ps) {
            String[] tmp = p.split("=");
            if (tmp.length <= 0) {
                continue;
            }

            if (tmp.length == 1) {
                paramsMap.put(tmp[0], StringUtils.EMPTY);
            } else {
                paramsMap.put(tmp[0], tmp[1]);
            }
        }

        return paramsMap;
    }

    /**
     * 给定一段URL后面的参数和一个key,返回key对应的值
     *
     * @param params 参数
     * @param key    key
     * @return 返回key对应的值
     */
    public static String getParam(String params, String key) {
        if (StringUtils.isEmpty(params) || StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }

        String[] ps = params.split("&");
        for (String p : ps) {
            String[] tmp = p.split("=");
            if (tmp.length <= 0) {
                continue;
            }

            if (key.equals(tmp[0])) {
                if (tmp.length == 1) {
                    return StringUtils.EMPTY;
                } else {
                    return tmp[1];
                }
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * 给定一段URL和一个key,返回key对应的值
     *
     * @param url url
     * @param key key
     * @return 返回key对应的值
     */
    public static String getParamFromUrl(String url, String key) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(key)) {
            return StringUtils.EMPTY;
        }

        if (!url.contains("?")) {
            return StringUtils.EMPTY;
        }

        String params = url.substring(url.indexOf("?") + 1);
        return getParam(params, key);
    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        Assert.notNull(requestAttributes);
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public static String getUserAgent(HttpServletRequest request) {
        return StringUtils.defaultString(request.getHeader("user-agent")).toLowerCase();
    }

    public static ClientType getClientType() {
        HttpServletRequest request = getCurrentRequest();
        String userAgent = getUserAgent(request);
        if (userAgent.lastIndexOf("mah/") > -1 || userAgent.lastIndexOf("thttpclient") > -1) {
            return ClientType.APP;
        } else if (userAgent.lastIndexOf("micromessenger") > -1) {
            return ClientType.WEIXIN;
        } else if (userAgent.lastIndexOf("alipayclient") > -1) {
            return ClientType.ALIPAY;
        } else if (userAgent.lastIndexOf("qq") > -1) {
            return ClientType.QQ;
        } else {
            return ClientType.BROWSER;
        }
    }
}
