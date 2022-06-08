package com.ice.framework.lock;

import com.ice.framework.exception.MgException;
import com.ice.framework.response.ResponseErrorCodeEnum;
import com.ice.framework.util.RedissLockUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

/**
 * Author: qiang.su
 * Date: 2021/5/26
 * Msg:
 */
@Slf4j
@Component
public class AspectDisLockHelper {

    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static Integer MIN_EXPIRE_TIME = 20;

    //加锁(from 切面)
    public static RLock lock(String lockKey, int waitTime, int expireTIme) {
        DoLockCallback callback = doLock(lockKey, waitTime, expireTIme, false);
        return callback.getRLock();
    }

    //加锁(from 声明式)
    public static AspectDisLockContextHolder.CheckLockCallback lockManually(String lockKey, int waitTime, int expireTIme) {
        DoLockCallback callback = doLock(lockKey, waitTime, expireTIme, true);
        return callback.getCheckLockCallback();
    }

    @Data
    private static class DoLockCallback {
        private RLock rLock;
        private AspectDisLockContextHolder.CheckLockCallback checkLockCallback;
    }

    public static DoLockCallback doLock(String lockKey, int waitTime, int expireTIme, boolean manually) {
        DoLockCallback callback = new DoLockCallback();
        //防止调用链太长，超时多给点
        expireTIme = expireTIme < MIN_EXPIRE_TIME ? MIN_EXPIRE_TIME : expireTIme;
        AspectDisLockContextHolder.KeyContent exist = AspectDisLockContextHolder.getExist(lockKey);
        RLock rLock;
        if (exist == null) {
            exist = new AspectDisLockContextHolder.KeyContent();
            try {
                log.info("before lock: " + lockKey);
                if (waitTime > 0) {
                    rLock = RedissLockUtil.tryLockGet(lockKey, waitTime, expireTIme);
                } else {
                    rLock = RedissLockUtil.lock(lockKey, expireTIme);
                }
                log.info("after lock: " + lockKey);
                if (null == rLock) {
                    throw new MgException(ResponseErrorCodeEnum.REDIS_LOCK_ERROR.getCode(), "获取锁失败");
                }
            } catch (MgException e) {
                log.error("获取锁失败：" + lockKey);
                throw new MgException(ResponseErrorCodeEnum.REDIS_LOCK_ERROR.getCode(), "业务频繁，请稍后再试！");
            } catch (Exception e) {
                log.error("获取锁异常[" + lockKey + "]：", e);
                throw new MgException(ResponseErrorCodeEnum.REDIS_LOCK_ERROR.getCode(), "业务过于频繁，请稍后再试！");
            }
            exist.setExpireTIme(expireTIme);
            exist.setKey(lockKey);
            exist.setLock(rLock);
            AspectDisLockContextHolder.setKeyIfAbsent(exist);
            //手动加锁要记录最初线程,只有这个线程能unlock锁链
            AspectDisLockContextHolder.CheckLockCallback checkLockCallback = AspectDisLockContextHolder.checkTopLock();
            if (manually) {
                callback.setCheckLockCallback(checkLockCallback);
            }
        } else {
            rLock = exist.getLock();
            callback.setCheckLockCallback(new AspectDisLockContextHolder.CheckLockCallback());
        }
        callback.setRLock(rLock);
        return callback;
    }


    //释放锁
    public static void unlock() {
        doUnlock();
        cleanContext();
    }

    //释放锁(当前线程是初始线程才能释放所有锁)
    public static void unlockManually(AspectDisLockContextHolder.CheckLockCallback manualLockCallback) {
        if (manualLockCallback.isCanUnlock()) {
            doUnlock();
            cleanContext();
        }
    }

    public static void doUnlock() {
        LinkedHashSet<AspectDisLockContextHolder.KeyContent> keysReversed = AspectDisLockContextHolder.getKeysReversed();
        for (AspectDisLockContextHolder.KeyContent keyContent :
                keysReversed) {
            if (null != keyContent) {
                String key = keyContent.getKey();
                try {
                    System.out.println("before unlock lock: " + key);
                    log.info("before unlock lock: " + key);
                    RedissLockUtil.unlock(key);
                    System.out.println("after unlock lock: " + key);
                    log.info("after unlock lock: " + key);
                } catch (Exception e) {
                    log.info("释放锁失败:" + key, e);
                }
            }
        }
    }


    //释放资源
    public static void cleanContext() {
        AspectDisLockContextHolder.clean();
    }
}
