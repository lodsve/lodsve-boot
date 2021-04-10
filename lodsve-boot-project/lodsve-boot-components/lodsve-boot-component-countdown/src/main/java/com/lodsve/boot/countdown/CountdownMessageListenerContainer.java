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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * countdown MessageListenerContainer.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 * @date 2017/12/17 上午12:34
 */
public class CountdownMessageListenerContainer extends RedisMessageListenerContainer {
    public CountdownMessageListenerContainer(RedisConnectionFactory connectionFactory, CountdownListener listener, List<Integer> databases) {
        super.setConnectionFactory(connectionFactory);
        List<Topic> topics = new ArrayList<>();
        if (CollectionUtils.isEmpty(databases)) {
            topics.add(new PatternTopic("__keyevent@*:expired"));
        } else {
            topics.addAll(databases.stream().map(d -> new PatternTopic("__keyevent@" + d + "__:expired")).collect(Collectors.toList()));
        }

        super.addMessageListener(listener, topics);
    }
}
