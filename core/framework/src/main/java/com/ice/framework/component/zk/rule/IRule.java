package com.ice.framework.component.zk.rule;

import java.util.List;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
public interface IRule {

    String chooseNode(List<String> addressList);
}
