package com.ice.framework.component.mq;

import com.ice.framework.component.tracing.TracingContext;
import com.ice.framework.exception.MgException;
import com.ice.framework.response.ResponseErrorCodeEnum;
import com.ice.framework.util.ObjectUtils;
import com.ice.framework.util.SpringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * @author : tjq
 * @since : 2022/3/9 14:30
 */
@Service
public class RocketMqSendUtil {

    private Logger log = LoggerFactory.getLogger(RocketMqSendUtil.class);

    @Value("${rocketmq.producer.send-message-timeout:#{null}}")
    private Integer messageTimeOut;

    /**
     * 发送延时消息（上面的发送同步消息，delayLevel的值就为0，因为不延时）
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    public SendResult sendDelayMsg(String topic, String msgBody, int delayLevel) {
        SendResult sendResult = SpringUtils.getBean(RocketMQTemplate.class).syncSend(topic, MessageBuilder.withPayload(msgBody).build(), messageTimeOut, delayLevel);
        log.info("消息发送结果:body:{},topic:{},level:{},result:{}", msgBody, topic, delayLevel, sendResult.getSendStatus());
        return sendResult;
    }

    /**
     * 发送延迟链路消息
     *
     * @param topic
     * @param msgBody
     * @param delayLevel
     * @return
     */
    public SendResult sendDelayTracingMsg(String topic, String msgBody, int delayLevel) {
        String tracingXid = TracingContext.tracingXid();
        Message<String> build;
        if (ObjectUtils.isNotEmpty(tracingXid)) {
            build = MessageBuilder.withPayload(msgBody).setHeader(TracingContext.TRACING_XID, tracingXid).build();
        } else {
            build = MessageBuilder.withPayload(msgBody).build();
        }
        SendResult sendResult = SpringUtils.getBean(RocketMQTemplate.class).syncSend(topic, build, messageTimeOut, delayLevel);
        log.warn("链路消息发送结果:body:{},topic:{},level:{}", msgBody, topic, delayLevel);
        return sendResult;
    }

    public void throwEx(SendResult result) {
        if (result.getSendStatus() != SendStatus.SEND_OK) {
            throw new MgException(ResponseErrorCodeEnum.MQ_ERROR.getCode(), "消息异步投递失败！");
        }
    }
}
