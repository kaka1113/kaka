package com.mg.framework.config;

import com.mg.framework.component.job.XxlJob;
import com.mg.framework.component.mongo.MongoEventListener;
import com.mg.framework.component.netty.RemoteClientService;
import com.mg.framework.component.netty.RemoteServerService;
import com.mg.framework.component.netty.RemoteService;
import com.mg.framework.component.redis.DefaultRedisListenerContainer;
import com.mg.framework.component.redis.ErrorHandlerListener;
import com.mg.framework.component.schema.AutoDBScript;
import com.mg.framework.component.tracing.WebConfig;
import com.mg.framework.component.zk.IClient;
import com.mg.framework.component.zk.IRedisClientImpl;
import com.mg.framework.component.zk.IZkClientImpl;
import com.mg.framework.util.SpringUtils;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
@Configuration
@ConditionalOnProperty(name = "mg.service.enabled", havingValue = "true", matchIfMissing = false)
public class ComponentConfig {
    private final Logger log = LoggerFactory.getLogger(ComponentConfig.class);

    @Value("${mg.zk.address:#{null}}")
    private String address;

    @Value("${mg.netty.server.port:#{null}}")
    private Integer serverPort;

    @Value("${mg.netty.client.port:#{null}}")
    private Integer clientPort;

    @Value("${mg.netty.client.ip:#{null}}")
    private String clientIp;


    /**
     * todo 需要根据不同的服务定义不同的key,现在所有的都用的同一个前缀
     * mongoKey主键生成
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.mongo.enabled", havingValue = "true", matchIfMissing = false)
    public MongoEventListener mongoEventListener() {
        return new MongoEventListener();
    }


    /**
     * redisKey失效监听
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.redis.expire.enabled", havingValue = "true", matchIfMissing = false)
    public DefaultRedisListenerContainer expireListener(RedisConnectionFactory connectionFactory) {
        //Redis消息监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        //设置Redis链接工厂
        container.setConnectionFactory(connectionFactory);
        //失效key消费异常兜底方案
        container.setErrorHandler(new ErrorHandlerListener());
        return new DefaultRedisListenerContainer(container);
    }

    /**
     * xxl-job定时任务
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.job.enabled", havingValue = "true", matchIfMissing = false)
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJob xxlJob = SpringUtils.getBean(XxlJob.class);
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJob.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(xxlJob.getAppname());
        xxlJobSpringExecutor.setAddress(xxlJob.getAddress());
        xxlJobSpringExecutor.setIp(xxlJob.getIp());
        xxlJobSpringExecutor.setPort(xxlJob.getPort());
        xxlJobSpringExecutor.setAccessToken(xxlJob.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJob.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJob.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }

    /**
     * 自定义链路追踪
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.tracing.enabled", havingValue = "true", matchIfMissing = false)
    public WebConfig trace() {
        return new WebConfig();
    }

    /**
     * 数据库对比插件
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.db.compare.enabled", havingValue = "true", matchIfMissing = false)
    public AutoDBScript dbScript() {
        return new AutoDBScript();
    }

    /**
     * zk注册中心
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.zk.enabled", havingValue = "true", matchIfMissing = false)
    public IClient zkClient() {
        log.info("zk 注册中心初始化：{}", address);
        return new IZkClientImpl(address);
    }

    /**
     * redis注册中心
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.redis.enabled", havingValue = "true", matchIfMissing = false)
    public IClient redisClient() {
        log.info("redis 注册中心初始化：{}", address);
        return new IRedisClientImpl(SpringUtils.getBean(RedissonClient.class));
    }

    /**
     * netty服务端
     *
     * @return
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnProperty(name = "mg.netty.enabled", havingValue = "true", matchIfMissing = false)
    public RemoteService remoteServer() {
        log.info("netty Server初始化：{}", serverPort);
        return new RemoteServerService(serverPort);
    }

    /**
     * netty客户端
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "mg.netty.client.enabled", havingValue = "true", matchIfMissing = false)
    public RemoteService remoteClient() {
        log.info("netty Client初始化：{}", clientPort);
        return new RemoteClientService(clientIp, clientPort);
    }


}
