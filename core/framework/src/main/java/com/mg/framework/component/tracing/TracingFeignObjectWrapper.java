package com.mg.framework.component.tracing;

import feign.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;


public class TracingFeignObjectWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(TracingFeignObjectWrapper.class);

    private final BeanFactory beanFactory;

    public TracingFeignObjectWrapper(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    Object wrap(Object bean) {
        if (bean instanceof Client && !(bean instanceof TracingFeignClient)) {
            return new TracingFeignClient(this.beanFactory, (Client) bean);
        }
        return bean;
    }
}
