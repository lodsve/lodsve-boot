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
package com.lodsve.boot.autoconfigure.rocketmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * rocketMQ consumer配置.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
@ConfigurationProperties(prefix = "lodsve.rocketmq")
public class RocketMqConsumerProperties {
    private String charset = "UTF-8";
    private Consumer consumer;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public static class Consumer {
        private String group = "DefaultConsumer";
        private int consumeThreadMin = 20;
        private int consumeThreadMax = 64;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public int getConsumeThreadMin() {
            return consumeThreadMin;
        }

        public void setConsumeThreadMin(int consumeThreadMin) {
            this.consumeThreadMin = consumeThreadMin;
        }

        public int getConsumeThreadMax() {
            return consumeThreadMax;
        }

        public void setConsumeThreadMax(int consumeThreadMax) {
            this.consumeThreadMax = consumeThreadMax;
        }
    }
}
