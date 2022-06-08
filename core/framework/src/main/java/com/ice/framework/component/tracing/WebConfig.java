package com.ice.framework.component.tracing;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TracingContextIntercept());
    }

    @Bean
    TracingFeignObjectWrapper tracingFeignObjectWrapper(BeanFactory beanFactory) {
        return new TracingFeignObjectWrapper(beanFactory);
    }

    @Bean
    TracingBeanPostProcessor tracingBeanPostProcessor(
            TracingFeignObjectWrapper tracingFeignObjectWrapper) {
        return new TracingBeanPostProcessor(tracingFeignObjectWrapper);
    }

}
