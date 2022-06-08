package com.ice.framework.component.zk;

import com.ice.framework.component.zk.rule.IRule;

import java.util.List;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
public interface IClient {


    /**
     * 服务注册
     *
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName, String serviceAddress);

    /**
     * 根据服务名称和规则查找服务地址
     * 没有注册节点去获取节点会报错
     *
     * @param serviceName
     * @return
     */
    String discoverRule(String serviceName, IRule iRule);

    /**
     * 查找该服务所有集群节点
     * 没有注册节点去获取节点会报错
     *
     * @param serviceName
     * @return
     */
    List<String> discoverAll(String serviceName);


}
