package com.mg.framework.component.netty.msg;

import com.mg.framework.component.netty.codec.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 需要保证线程安全
 *
 * @author : tjq
 * @since : 2022-05-12
 */
@ChannelHandler.Sharable
public class MsgHandler extends SimpleChannelInboundHandler<Message> {

    private RpcAnswer rpcAnswer;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        rpcAnswer.callback(message);
    }
}
