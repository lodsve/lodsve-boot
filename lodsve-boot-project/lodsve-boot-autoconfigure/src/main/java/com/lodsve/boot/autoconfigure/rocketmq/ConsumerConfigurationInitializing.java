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

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 动态注册消费者.
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class ConsumerConfigurationInitializing implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerConfigurationInitializing.class);
    private static final AtomicLong COUNTER = new AtomicLong(0);
    private ConfigurableApplicationContext applicationContext;
    private RocketMQProperties properties;
    private RocketMqConsumerProperties consumerProperties;
    private Environment environment;

    @Override
    public void afterSingletonsInstantiated() {
        // 配置
        properties = applicationContext.getBean(RocketMQProperties.class);
        consumerProperties = applicationContext.getBean(RocketMqConsumerProperties.class);

        // 消费者处理类
        Map<String, Object> handlers = applicationContext.getBeansWithAnnotation(MessageHandler.class);

        handlers.forEach((k, v) -> resolveHandlers(k, v, consumerProperties.getConsumer().getConsumeThreadMin(), consumerProperties.getConsumer().getConsumeThreadMax()));
    }

    private void resolveHandlers(String beanName, Object bean, int consumeThreadMin, int consumeThreadMax) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);

        MessageHandler handler = clazz.getAnnotation(MessageHandler.class);
        String group = environment.resolvePlaceholders(handler.group());
        String topic = environment.resolvePlaceholders(handler.topic());

        // 获取所有含有MessageListener注解的方法
        Method[] methods = clazz.getMethods();
        List<ConsumerBean> consumerBeans = Arrays.stream(methods).filter(m -> m.isAnnotationPresent(MessageListener.class)).map(m -> {
            MessageListener listener = m.getAnnotation(MessageListener.class);
            String tag = environment.resolvePlaceholders(listener.tag());

            ConsumerBean consumerBean = new ConsumerBean();
            consumerBean.setMethod(m);
            consumerBean.setTarget(bean);
            consumerBean.setTag(tag);
            return consumerBean;
        }).collect(Collectors.toList());

        DefaultMQPushConsumer consumer = createConsumer(group, topic, consumerBeans, consumeThreadMin, consumeThreadMax);

        // 注册到spring上下文
        String consumerBeanName = String.format("%s_%s_%s", beanName, DefaultMQPushConsumer.class.getSimpleName(), COUNTER.incrementAndGet());
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        genericApplicationContext.registerBean(consumerBeanName, DefaultMQPushConsumer.class, () -> consumer);
    }

    private DefaultMQPushConsumer createConsumer(String group, String topic, List<ConsumerBean> consumerBeans, int consumeThreadMin, int consumeThreadMax) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(properties.getNameServer());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);

        // 订阅topic，包含所有的tag
        try {
            consumer.subscribe(topic, "*");
            registerMessageListener(consumer, consumerBeans);
            consumer.start();
            return consumer;
        } catch (MQClientException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private void registerMessageListener(DefaultMQPushConsumer consumer, List<ConsumerBean> consumerBeans) {
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {
            boolean result;
            for (MessageExt m : messages) {
                String tag = m.getTags();
                if (StringUtils.isBlank(tag)) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                try {
                    List<ConsumerBean> bean = consumerBeans.stream().filter(cb -> StringUtils.equals(cb.getTag(), tag)).collect(Collectors.toList());

                    if (CollectionUtils.isEmpty(bean)) {
                        // 匹配不到监听
                        continue;
                    }

                    ConsumerBean b = bean.get(0);
                    Method method = b.getMethod();
                    Object target = b.getTarget();

                    Type[] parameterTypes = method.getGenericParameterTypes();
                    if (ArrayUtils.isEmpty(parameterTypes)) {
                        logger.warn("handler has no parameter types!tag is '[{}]'! message '[{}]' will be push back to rocketmq!", tag, m);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }

                    if (1 < parameterTypes.length) {
                        logger.warn("handler has more than one parameter types, consumer will use the first type!");
                    }

                    Object invokeResult = method.invoke(target, doConvertMessage(parameterTypes[0], m, consumerProperties.getCharset()));
                    result = (invokeResult instanceof Boolean && (boolean) invokeResult);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                if (!result) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    private Object doConvertMessage(Type messageType, MessageExt messageExt, String charset) {
        String json = new String(messageExt.getBody(), Charset.forName(charset));

        if (!(messageType instanceof ParameterizedType)) {
            return doConvertMessageWithoutGeneric((Class<?>) messageType, json);
        }

        // 存在泛型
        return JSON.parseObject(json, messageType);
    }

    private Object doConvertMessageWithoutGeneric(Class<?> messageType, String json) {
        if (Objects.equals(messageType, String.class)) {
            return json;
        } else {
            // If msgType not string, use JSON change it.
            try {
                return JSON.parseObject(json, messageType);
            } catch (Exception e) {
                throw new RuntimeException("cannot convert message to " + messageType, e);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        Map<String, DefaultMQPushConsumer> consumers = applicationContext.getBeansOfType(DefaultMQPushConsumer.class);
        consumers.values().forEach(DefaultMQPushConsumer::shutdown);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
