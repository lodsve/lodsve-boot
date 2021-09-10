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
package com.lodsve.boot.countdown;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.Serializable;

/**
 * redis定时器的监听.<br/>
 * redis必须在配置文件redis.conf中设置为:<code>notify-keyspace-events Ex</code><br/>
 * also see <a href="http://blog.csdn.net/chaijunkun/article/details/27361453">Redis的Keyspace notifications功能初探</a>
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 15/9/28 下午4:11
 */
public class CountdownListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(CountdownListener.class);

    private final ApplicationEventPublisher eventPublisher;
    private final CountdownEventResolver eventResolver;

    public CountdownListener(ApplicationEventPublisher eventPublisher, CountdownEventResolver eventResolver) {
        this.eventPublisher = eventPublisher;
        this.eventResolver = eventResolver;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        byte[] messageChannel = message.getChannel();
        byte[] messageBody = message.getBody();

        String channel = new String(messageChannel);
        if (!channel.contains(CountdownConstants.REDIS_KEY_WORD)) {
            return;
        }
        String body = new String(messageBody);
        if (!body.startsWith(CountdownConstants.REDIS_KEY_PREFIX)) {
            return;
        }

        String[] temp = StringUtils.split(body, CountdownConstants.REDIS_KEY_SEPARATOR);

        String type = temp[1], key = temp[2];

        CountdownEventHandler handler = eventResolver.resolveEventHandler(type);
        Serializable id = handler.resolveKey(key);

        if (id == null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Publishing Event for order " + id);
        }

        this.eventPublisher.publishEvent(new CountdownEvent(this, id, handler.getEventType()));
    }
}
