package com.mg.framework.component.zk;

import com.mg.framework.component.zk.rule.IRule;
import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
public class IRedisClientImpl implements IClient {

    private final RedissonClient redissonClient;

    public IRedisClientImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void register(String serviceName, String serviceAddress) {

    }

    @Override
    public String discoverRule(String serviceName, IRule iRule) {
        return null;
    }

    @Override
    public List<String> discoverAll(String serviceName) {
        return null;
    }
}
