package com.ice.framework.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Author: qiang.su
 * Date: 2020/4/1
 * Msg:
 */
public class RedissonDistributedLocker implements DistributedLocker {

    private final Logger logger = LoggerFactory.getLogger(RedissonDistributedLocker.class);

    private RedissonClient redissonClient;

    public RedissonDistributedLocker(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    @Override
    public RLock lock(String lockKey, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public RLock tryLockGet(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            //boolean b = lock.tryLock(waitTime, leaseTime, unit);
            //开启看门狗
            boolean b = lock.tryLock(waitTime, unit);
            if (b) {
                return lock;
            } else {
                logger.error("lockKey:{},获取锁状态：{}", lockKey, b);
                return null;
            }
        } catch (InterruptedException e) {
            logger.error("加锁异常", e);
            return null;
        }
    }

    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.unlock();
        } catch (Exception e) {
            logger.error("自动释放锁失败:" + lockKey);
            throw e;
        }
    }

    @Override
    public void unlock(RLock lock) {
        lock.unlock();
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

}
