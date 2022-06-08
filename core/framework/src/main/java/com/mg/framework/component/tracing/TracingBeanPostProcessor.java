package com.mg.framework.component.tracing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class TracingBeanPostProcessor implements BeanPostProcessor {

    private final TracingFeignObjectWrapper tracingFeignObjectWrapper;

    public TracingBeanPostProcessor(TracingFeignObjectWrapper tracingFeignObjectWrapper) {
        this.tracingFeignObjectWrapper = tracingFeignObjectWrapper;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return this.tracingFeignObjectWrapper.wrap(bean);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
