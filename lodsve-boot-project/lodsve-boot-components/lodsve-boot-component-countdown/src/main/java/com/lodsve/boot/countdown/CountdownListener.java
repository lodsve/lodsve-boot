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
