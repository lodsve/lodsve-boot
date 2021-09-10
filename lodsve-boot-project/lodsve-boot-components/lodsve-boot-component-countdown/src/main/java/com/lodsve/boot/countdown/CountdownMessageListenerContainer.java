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
