package com.ice.framework.component.netty.msg;

import com.ice.framework.component.netty.codec.Message;

/**
 * @author : tjq
 * @since : 2022-05-12
 */
public interface RpcAnswer {

    void callback(Message rpcCmd);
}
