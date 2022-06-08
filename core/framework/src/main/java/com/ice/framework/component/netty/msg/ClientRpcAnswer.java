package com.ice.framework.component.netty.msg;

import com.ice.framework.component.thread.ThreadPoolUtils;
import com.ice.framework.component.netty.codec.Message;

/**
 * @author : tjq
 * @since : 2022-05-12
 */
public class ClientRpcAnswer implements RpcAnswer {
    @Override
    public void callback(Message rpcCmd) {
        ThreadPoolUtils.submit(() -> {

        });
    }
}
