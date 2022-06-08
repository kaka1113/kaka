package com.mg.framework.aop;


import com.mg.framework.annotation.DistributionLock;
import com.mg.framework.lock.AspectDisLockContextHolder;
import com.mg.framework.lock.AspectDisLockHelper;
import com.mg.framework.lock.AspectDisLockKeyParser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 场景说明：
 * 1.由于扣库存底层方法存在多处引用，扣库存入口较多，其中一个扣掉库存，释放锁，事物未提交，其他线程查询到扣减之前的数据，产生超扣库存
 * 2.上层分布式锁方法和被调用扣库存方法循环上锁，只有发起者才能释放全局锁，避免底层库存超扣的情况
 * Author: qiang.su
 * Date: 2021/5/26
 * Msg: 分布式注解处理
 */
@Slf4j
@Aspect
@Component
@Order(0)
public class DisLockAspect {

    @Around(value = "@annotation(disLock)")
    public Object execute(ProceedingJoinPoint proceedingJoinPoint, DistributionLock disLock) throws Throwable {
        int expireTIme = disLock.expireTime();
        int waitTime = disLock.waitTime();
        Object proceed;

        //第一个锁发起方释放锁
        boolean needUnlock = AspectDisLockContextHolder.isEmpty();

        String[] keys = disLock.key();
        try {
            for (int i = 0; i < keys.length; i++) {
                toLock(proceedingJoinPoint, expireTIme, waitTime, keys[i]);
            }
            proceed = proceedingJoinPoint.proceed();
        } finally {
            if (needUnlock) {
                log.info(Thread.currentThread().getId() + " 锁发起者unlock...");
                AspectDisLockHelper.unlock();
            }
        }
        return proceed;
    }

    private void toLock(ProceedingJoinPoint proceedingJoinPoint, int expireTIme, int waitTime, String key) throws Throwable {
        String lockKey = AspectDisLockKeyParser.parse(proceedingJoinPoint, key);
        AspectDisLockHelper.lock(lockKey, waitTime, expireTIme);
    }

    //TODO 代码规范性报错：锁外面不能嵌套不带锁的事务
    //TODO 带分布式事务的解决方案

}
