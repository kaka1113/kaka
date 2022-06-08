package com.mg.framework.component.netty.msg;

import com.mg.framework.component.netty.codec.Message;

/**
 * @author : tjq
 * @since : 2022-05-12
 */
public interface RpcAnswer {

    void callback(Message rpcCmd);
}
