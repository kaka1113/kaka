package com.mg.framework.component.netty.msg;

import com.mg.framework.component.netty.codec.Message;
import com.mg.framework.component.thread.ThreadPoolUtils;

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
