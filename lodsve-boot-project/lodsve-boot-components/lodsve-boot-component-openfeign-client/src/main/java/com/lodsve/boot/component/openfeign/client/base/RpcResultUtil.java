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
package com.lodsve.boot.component.openfeign.client.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * 用来处理rpc返回结果的共通处理类.
 *
 * @author Hulk Sun
 */
public class RpcResultUtil {
    private static final Logger logger = LoggerFactory.getLogger(RpcResultUtil.class);

    /**
     * 判断rpcResult是否可用,出错会抛异常
     *
     * @param rpcResult rpc请求的返回结果对象,包含数据和调用是否成功,错误信息
     * @return 是否是成功
     */
    public static boolean isOk(RpcResult<?> rpcResult) {
        return isOk(rpcResult, true);
    }

    /**
     * 判断rpcResult是否可用,根据参数决定是否要抛出异常
     *
     * @param rpcResult        rpc请求的返回结果对象,包含数据和调用是否成功,错误信息
     * @param isThrowException 是否需要抛出异常
     * @return 是否是成功
     */
    public static boolean isOk(RpcResult<?> rpcResult, boolean isThrowException) {
        if (rpcResult == null) {
            if (isThrowException) {
                logger.error("rpcResult为null");
                throw new RpcException("rpcResult为null");

            }
            return false;
        } else {
            //422校验不通过,直接显示rpcResult服务方返回的error信息
            if (String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()).equals(rpcResult.getCode())) {
                if (isThrowException) {
                    logger.error("rpcResult校验未通过,返回结果为[{}],异常信息为[{}]", rpcResult, rpcResult.getError());
                    throw new RpcException(rpcResult.getError());
                } else {
                    return false;
                }
            } else {
                if (RpcResult.CODE_SUCCESS.equals(rpcResult.getCode())) {
                    return true;
                } else {
                    if (isThrowException) {
                        logger.error("rpcResult调用失败,返回结果为[{}]", rpcResult);
                        throw new RpcException(rpcResult.toString());
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    /**
     * 获取data,出错会抛异常
     *
     * @param rpcResult rpc请求的返回结果对象,包含数据和调用是否成功,错误信息
     * @param <T>       返回对象类型
     * @return result中的返回对象
     */
    public static <T> T getRpcData(RpcResult<T> rpcResult) {
        return getRpcData(rpcResult, true);
    }

    /**
     * 获取data,根据参数决定是否要抛出异常
     *
     * @param rpcResult        rpc请求的返回结果对象,包含数据和调用是否成功,错误信息
     * @param isThrowException 是否需要抛出异常
     * @param <T>              返回对象类型
     * @return result中的返回对象
     */
    public static <T> T getRpcData(RpcResult<T> rpcResult, boolean isThrowException) {
        if (isOk(rpcResult, isThrowException)) {
            if (null != rpcResult.getData()) {
                return rpcResult.getData();
            } else {
                if (isThrowException) {
                    logger.error("rpcResult data为null");
                    throw new RpcException("rpcResult data为null");
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

}
