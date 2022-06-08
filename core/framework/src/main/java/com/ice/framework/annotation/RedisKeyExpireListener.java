package com.ice.framework.annotation;

import com.ice.framework.enums.ModuleEnum;
import com.ice.framework.enums.ProjectEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * mg:expire:topic:oms:ordertimeout:1120220518
 * <p>
 * 说明：
 * 1.失效key是广播通知的，请业务做好一致性处理
 * 2.参数说明：
 * <p>
 * mg  所属项目
 * expire:topic 失效key订阅标识
 * oms  所属项目中的模块
 * <p>
 * <p>
 * ordertimeout  消费者订阅的字符串
 * 1120220518   业务中的订单号标识
 * <p>
 * 前面的key是必须的，后面的长度可以延伸，不做限制
 * <p>
 *
 * @author tjq
 * @since 2022/5/17 18:47
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RedisKeyExpireListener {

    /**
     * 所属项目
     *
     * @return
     */
    ProjectEnum project();

    /**
     * 项目下的模块
     *
     * @return
     */
    ModuleEnum module();


    /**
     * 监听key中包含的值
     *
     * @return
     */
    String value();


}
