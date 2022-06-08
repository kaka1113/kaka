package com.ice.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author:lyl
 * @Description:
 * @Date: Create in 17:22 2022/4/3
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    //核心线程数
    private int corePoolSize = 5;
    //最大线程数
    private int maxPoolSize = 50;
    //队列大小
    private int queueCapacity = 200;

    @Bean
    public Executor taskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setThreadNamePrefix("Thread:mg-");
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(300);
        return threadPoolTaskExecutor;
    }

}
