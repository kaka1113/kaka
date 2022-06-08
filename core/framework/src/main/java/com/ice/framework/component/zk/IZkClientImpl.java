package com.ice.framework.component.zk;

import com.ice.framework.component.zk.rule.IRule;
import com.ice.framework.util.ObjectUtils;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
public class IZkClientImpl implements IClient {

    private Logger log = LoggerFactory.getLogger(IZkClientImpl.class);

    private final ZkClient zkClient;


    public IZkClientImpl(String zkAddress) {
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(zkAddress, 5000, 1000);
        log.debug("connect zookeeper");
    }

    @Override
    public String discoverRule(String serviceName, IRule iRule) {
        // 获取 service 节点
        List<String> addressList = addressList(serviceName);
        addressList(serviceName);
        // 获取 address 节点
        String address;
        int size = addressList.size();
        if (size == 1) {
            // 若只有一个地址，则获取该地址
            address = addressList.get(0);
            log.debug("get only address node: {}", address);
        } else {
            address = iRule.chooseNode(addressList);
            log.debug("get random address node: {}", address);
        }
        return address;

    }

    /**
     * 根据规则获取
     *
     * @param serviceName
     * @return
     */
    @Override
    public List<String> discoverAll(String serviceName) {
        return addressList(serviceName);
    }

    public List<String> addressList(String serviceName) {
        // 获取 service 节点
        String servicePath = "/registry" + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
        }
        List<String> addressList = zkClient.getChildren(servicePath);
        if (ObjectUtils.isEmpty(addressList)) {
            throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
        }
        return addressList.stream().map(item -> (String) zkClient.readData(servicePath + "/" + item)).collect(Collectors.toList());
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = "/registry";
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            log.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, serviceAddress);
            log.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        log.debug("create address node: {}", addressNode);
    }
}
