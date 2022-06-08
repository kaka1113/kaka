//package com.mg.framework.component.redis;
//
//import com.mg.framework.annotation.RedisKeyExpireListener;
//import com.mg.framework.enums.ModuleEnum;
//import com.mg.framework.enums.ProjectEnum;
//import com.mg.framework.exception.MgException;
//import com.mg.framework.response.ResponseErrorCodeEnum;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
///** Demo
// * @author tjq
// * @since 2022/5/17 18:48
// */
//@Service
//public class RedisKeyExpireService {
//
//    private Logger logger = LoggerFactory.getLogger(RedisKeyExpireService.class);
//
//    /**
//     * 注解重复时只有一个生效
//     */
//    @RedisKeyExpireListener(project = ProjectEnum.MG, module = ModuleEnum.OMS, value = "ordertimeout")
//    public class RedisKeyExpireConsumer implements RedisExpireListener {
//
//        @Override
//        public void msg(String var1) {
//            logger.info("redisKey失效消费者接收到消息：{}", var1);
//            throw new MgException(ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getCode(), "测试异常");
//        }
//    }
//}
