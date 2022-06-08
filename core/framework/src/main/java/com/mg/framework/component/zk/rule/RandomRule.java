package com.mg.framework.component.zk.rule;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
public class RandomRule implements IRule {

    @Override
    public String chooseNode(List<String> addressList) {
        return addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
    }
}
