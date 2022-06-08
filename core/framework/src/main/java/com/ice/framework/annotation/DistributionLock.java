package com.ice.framework.annotation;

import java.lang.annotation.*;

/**
 * Author: qiang.su
 * Date: 2021/5/26
 * Msg: 分布式锁
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributionLock {

    /**
     * 分布式锁的key :
     * 格式为： a:b:{user.id}:{sku.id}  。 user.id来源于入参的属性
     *
     * @return
     */
    String[] key();

    /**
     * 过期时间, 默认是5秒
     * 单位是秒
     *
     * @return
     */
    int expireTime() default 10;

    /**
     * 尝试获取锁等待时间s
     *
     * @return
     */
    int waitTime() default 20;


    //tip：使用方式：
    /**
     *     @DistributionLock(key = "test:key1",expireTime = 5)
     *     @Transactional(rollbackFor = Exception.class)
     *     @Override
     *     public ResultModel<Long> test() {
     *         couponInfoMapper.updateA(100);
     *         Customer c = new Customer();
     *         c.setId(100L);
     *         c.setNickName("200");
     *         //方法内部调用要这么使用
     *         SpringUtils.getBean(this.getClass()).updateB(c,1);
     *         return ResultUtil.success(0);
     *     }
     *
     *     @DistributionLock(key = "test:key2:{c.id}:{c.nickName}:{a}",expireTime = 5)
     *     @Override
     *     public void updateB(Customer c,Integer a) {
     *         couponInfoMapper.updateB(200);
     *     }
     */
}
