package com.mg.framework.config;

import com.mg.framework.lock.DistributedLocker;
import com.mg.framework.lock.RedissonDistributedLocker;
import com.mg.framework.util.RedissLockUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Author: qiang.su
 * Date: 2020/4/1
 * Msg:
 */

@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfig {
    private static final Logger log = LoggerFactory.getLogger(RedissonAutoConfig.class);

    @Autowired
    private RedissonProperties redssionProperties;

    /**
     * 单机模式自动装配
     * 配置例子
     * redisson.address=redis://10.18.75.115:6379
     * redisson.password=
     * //这里如果不加redis://前缀会报URI构建错误
     * //其次，在redis进行连接的时候如果不对密码进行空判断，会出现AUTH校验失败的情况。
     *
     * @return
     */
    @Bean
//    @ConditionalOnProperty(name = "redisson.address")
//    @Primary
    RedissonClient redissonSingle() {
        log.info("init RedissonClient.redissonSingle()...");
        Config config = new Config();
        String host = redssionProperties.getHost();
        String port = redssionProperties.getPort();
        String address = "";
        if (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(port)) {
            address = "redis://" + host + ":" + port;
        }
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(address)
                .setTimeout(redssionProperties.getTimeout())
                .setConnectionPoolSize(redssionProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redssionProperties.getConnectionMinimumIdleSize())
                .setDatabase(redssionProperties.getDatabase());

        if (StringUtils.isNotBlank(redssionProperties.getPassword())) {
            serverConfig.setPassword(redssionProperties.getPassword());
        }
        //解决存储乱码问题
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

//    /**
//     * 哨兵模式自动装配    qiang.su: 如果要使用Redisson，最好做成1主2从的部署结构：(sentinel.conf中的“法定人数”，建议调整成2)
//     * 配置例子
//     * redisson.master-name=mymaster
//     * redisson.password=xxxx
//     * redisson.sentinel-addresses=10.47.91.83:26379,10.47.91.83:26380,10.47.91.83:26381
//     * @return
//     */
//    @Bean
//    @ConditionalOnProperty(name="redisson.master-name")
//    RedissonClient redissonSentinel() {
//        Config config = new Config();
//
//        String host = redssionProperties.getHost();
//        String port = redssionProperties.getPort();
//        String password = redssionProperties.getPassword();
//
//
//        config.useClusterServers().addNodeAddress();
//
//        SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redssionProperties.getSentinelAddresses())
//                .setMasterName(redssionProperties.getMasterName())
//                .setTimeout(redssionProperties.getTimeout())
//                .setMasterConnectionPoolSize(redssionProperties.getMasterConnectionPoolSize())
//                .setSlaveConnectionPoolSize(redssionProperties.getSlaveConnectionPoolSize());
//                //TODO要补充一些参数
//
//        if(StringUtils.isNotBlank(redssionProperties.getPassword())) {
//            serverConfig.setPassword(redssionProperties.getPassword());
//        }
//        return Redisson.create(config);
//    }


//    @Bean
//    @ConditionalOnProperty(name="redisson.master-name")
//    RedissonClient redissonCluster() {
//        Config config = new Config();
//        SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redssionProperties.getSentinelAddresses())
//                .setMasterName(redssionProperties.getMasterName())
//                .setTimeout(redssionProperties.getTimeout())
//                .setMasterConnectionPoolSize(redssionProperties.getMasterConnectionPoolSize())
//                .setSlaveConnectionPoolSize(redssionProperties.getSlaveConnectionPoolSize());
//        //TODO要补充一些参数
//
//        if(StringUtils.isNotBlank(redssionProperties.getPassword())) {
//            serverConfig.setPassword(redssionProperties.getPassword());
//        }
//        return Redisson.create(config);
//    }

    /**
     * 装配locker类，并将实例注入到RedissLockUtil中
     *
     * @return
     */
    @Bean
    DistributedLocker distributedLocker(RedissonClient redissonClient) {
        DistributedLocker locker = new RedissonDistributedLocker(redissonClient);
        RedissLockUtil.setLocker(locker);
        return locker;
    }

}
