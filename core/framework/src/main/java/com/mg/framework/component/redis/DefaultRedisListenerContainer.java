package com.mg.framework.component.redis;

import com.mg.framework.annotation.RedisKeyExpireListener;
import com.mg.framework.enums.ModuleEnum;
import com.mg.framework.enums.ProjectEnum;
import com.mg.framework.exception.MgException;
import com.mg.framework.response.ResponseErrorCodeEnum;
import com.mg.framework.util.ObjectUtils;
import com.mg.framework.util.json.JsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 监听所有db的过期事件__keyevent@*__:expired"
 * <p>
 * 默认配置notify-keyspace-events的值为" ",修改为 notify-keyspace-events Ex 这样便开启了过期事件
 *
 * @author tjq
 * @since 2022/5/17 19:24
 */
public class DefaultRedisListenerContainer extends KeyExpirationEventMessageListener implements ApplicationContextAware, SmartInitializingSingleton {

    private final Logger log = LoggerFactory.getLogger(DefaultRedisListenerContainer.class);

    private ConfigurableApplicationContext applicationContext;

    public DefaultRedisListenerContainer(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    private static final Map<String, Object> redisKeyListener = new HashMap<>();

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beansWithAnnotation = this.applicationContext.getBeansWithAnnotation(RedisKeyExpireListener.class);
        if (beansWithAnnotation.size() == 0) {
            log.info("未订阅redis失效key");
            return;
        }
        List<RedisKeyExpireListener> annotationList = new ArrayList<>();
        beansWithAnnotation.forEach((key, value) -> {
            Class<?> clazz = AopProxyUtils.ultimateTargetClass(value);
            if (RedisExpireListener.class.isAssignableFrom(value.getClass())) {
                RedisKeyExpireListener annotation = (RedisKeyExpireListener) clazz.getAnnotation(RedisKeyExpireListener.class);
                //获取所属项目
                ProjectEnum project = annotation.project();
                //获取所属模块
                ModuleEnum module = annotation.module();
                //获取监听的key
                String value1 = annotation.value();
                if (ObjectUtils.isEmpty(project)) {
                    throw new MgException(ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getCode(), "失效key监听所属项目不能为空！");
                }
                if (ObjectUtils.isEmpty(module)) {
                    throw new MgException(ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getCode(), "失效key监听所属模块不能为空！");
                }
                if (ObjectUtils.isEmpty(value1)) {
                    throw new MgException(ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getCode(), "失效key监听key不能为空！");
                }
                annotationList.add(annotation);
                redisKeyListener.put(key,value);
            }
        });
        //订阅项目
        Map<ProjectEnum, List<RedisKeyExpireListener>> projectEnumListMap = annotationList.stream().collect(Collectors.groupingBy(RedisKeyExpireListener::project));
        if (projectEnumListMap.size() > 1) {
            throw new MgException(ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getCode(), "订阅项目大于1个");
        }
        //订阅模块
        Map<ModuleEnum, List<RedisKeyExpireListener>> modules = annotationList.stream().collect(Collectors.groupingBy(RedisKeyExpireListener::module));
        if (modules.size() > 1) {
            throw new MgException(ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getCode(), "订阅模块大于1个");
        }
        log.info("redis expire listener:{}", JsonFactory.jsonTools().getJson(redisKeyListener));
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 获取到失效的 key，进行取消订单业务处理
        String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("key已经失效：{}", expiredKey);
        String[] split = expiredKey.split(":");
        if (!expiredKey.contains("expire:topic")) {
            log.warn("该失效key非失效订阅key");
        }
        if (split.length < 6) {
            log.warn("失效key长度至少六位");
        }
        ProjectEnum projectEnum = ProjectEnum.keyOf(split[0]);
        if (ObjectUtils.isEmpty(projectEnum)) {
            log.warn("失效key获取项目失效模块失败！");
            return;
        }
        ModuleEnum moduleEnum = ModuleEnum.keyOf(split[3]);
        if (ObjectUtils.isEmpty(moduleEnum)) {
            log.warn("未获取到失效key的模块");
            return;
        }
        String listenerKey = split[4];
        RedisExpireListener listener = (RedisExpireListener)redisKeyListener.get(listenerKey);
        log.info("所属项目：{},所属模块：{},订阅key:{}", projectEnum, moduleEnum, expiredKey);
        if(ObjectUtils.isNotEmpty(listener)){
            listener.msg(listenerKey);
        }
    }


}
