package com.ice.framework.component.job;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */
@Data
@Configuration
public class XxlJob {
    private Logger logger = LoggerFactory.getLogger(XxlJob.class);

    @Value("${mg.job.admin.addresses:#{null}}")
    private String adminAddresses;

    @Value("${mg.job.accessToken:#{null}}")
    private String accessToken;

    @Value("${mg.job.executor.appname:#{null}}")
    private String appname;

    @Value("${mg.job.executor.address:#{null}}")
    private String address;

    @Value("${mg.job.executor.ip:#{null}}")
    private String ip;

    @Value("${mg.job.executor.port:#{null}}")
    private Integer port;

    @Value("${mg.job.executor.logpath:#{null}}")
    private String logPath;

    @Value("${mg.job.executor.logretentiondays:#{null}}")
    private Integer logRetentionDays;


//    @Bean
//    public XxlJobSpringExecutor xxlJobExecutor() {
//        logger.info(">>>>>>>>>>> xxl-job config init.");
//        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
//        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
//        xxlJobSpringExecutor.setAppname(appname);
//        xxlJobSpringExecutor.setAddress(address);
//        xxlJobSpringExecutor.setIp(ip);
//        xxlJobSpringExecutor.setPort(port);
//        xxlJobSpringExecutor.setAccessToken(accessToken);
//        xxlJobSpringExecutor.setLogPath(logPath);
//        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
//
//        return xxlJobSpringExecutor;
//    }

    /**
     * 针对多网卡、容器内部署等情况，可借助 "spring-cloud-commons" 提供的 "InetUtils" 组件灵活定制注册IP；
     *
     *      1、引入依赖：
     *          <dependency>
     *             <groupId>org.springframework.cloud</groupId>
     *             <artifactId>spring-cloud-commons</artifactId>
     *             <version>${version}</version>
     *         </dependency>
     *
     *      2、配置文件，或者容器启动变量
     *          spring.cloud.inetutils.preferred-networks: 'xxx.xxx.xxx.'
     *
     *      3、获取IP
     *          String ip_ = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
     */


}