package com.ice.framework.component.tracing;

import com.ice.framework.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

public class TracingContext {

    private static final Logger log = LoggerFactory.getLogger(TracingContext.class);

    public static final String TRACING_XID = "TRACING_XID";

    public static final String SERVICE_NAME = "SERVICE_NAME";

    /**
     * 强制打印日志
     */
    public static final String FORCE_LOG = "FORCE_LOG";


    /**
     * mq生产者绑定消息
     */
    public static String tracingXid() {
        return MDC.get(TracingContext.TRACING_XID);
    }


    /**
     * 消费者绑定tracingId
     *
     * @param tracingXid
     */
    public static void mqConsumerBuildTracingXid(String tracingXid) {
        if (ObjectUtils.isNotEmpty(tracingXid)) {
            log.info("Mq链路id已绑定：{}", tracingXid);
            MDC.put(TracingContext.TRACING_XID, tracingXid);

        } else {
            tracingXid = UUID.randomUUID().toString().replace("-", "");
            log.info("Mq链路id生成：{}", tracingXid);
            MDC.put(TracingContext.TRACING_XID, tracingXid);
        }
    }

    /**
     * 消费者生成链路id
     * xxlMq、rocketMq
     */
    public static void componentTracingXid() {
        String tracingXid = MDC.get(TracingContext.TRACING_XID);
        if (ObjectUtils.isEmpty(tracingXid)) {
            tracingXid = UUID.randomUUID().toString().replace("-", "");
            log.info("Mq链路id生成：{}", tracingXid);
            MDC.put(TracingContext.TRACING_XID, tracingXid);
        }
    }

    /**
     * 消费者执行完移除链路id
     * xxlMq、rocketMq
     */
    public static void removeComponentTracingXid() {
        String tracingXid = MDC.get(TracingContext.TRACING_XID);
        if (ObjectUtils.isNotEmpty(tracingXid)) {
            log.info("Mq链路id结束：{}", tracingXid);
            MDC.remove(TracingContext.TRACING_XID);
        }
    }




}
