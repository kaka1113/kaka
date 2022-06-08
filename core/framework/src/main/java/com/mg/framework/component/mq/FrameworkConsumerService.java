package com.mg.framework.component.mq;

import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;

/**
 * @author : tjq
 * @since : 2022-05-12
 */
public abstract class FrameworkConsumerService<T> implements RocketMQListener<T>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void onMessage(T msg) {
        try {
            //统一链路处理
            //统一加锁处理
            process(msg);
        } finally {
            //释放链路
            //释放锁
        }
    }


    protected abstract void process(T msg);


}
