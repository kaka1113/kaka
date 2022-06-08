package com.ice.framework.component.netty.msg;

import com.ice.framework.component.netty.codec.Message;
import com.ice.framework.component.thread.ThreadPoolUtils;

/**
 * @author : tjq
 * @since : 2022-05-12
 */
public class ServerRpcAnswer  implements RpcAnswer{


    @Override
    public void callback(Message rpcCmd) {
        ThreadPoolUtils.submit(()->{

        });
    }
}
