package com.mg.framework.component.netty;

import com.mg.framework.component.netty.codec.MessageDecoder;
import com.mg.framework.component.netty.codec.MessageEncoder;
import com.mg.framework.component.netty.config.NettyConfig;
import com.mg.framework.component.netty.msg.MsgHandler;
import com.mg.framework.component.netty.msg.SocketManagerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

/**
 * @author : tjq
 * @since : 2022-05-11
 */
public class RemoteServerService implements RemoteService {

    private Integer port;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public RemoteServerService(Integer port) {
        this.port = port;
        //1.创建eventLoopGroup
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void init() {
        try {
            //2.创建serverBootStrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//3.指定所使用的NIO传输channel
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .localAddress(new InetSocketAddress(port))//4.绑定本地套接字端口
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() {//5.添加EchoServerHandler到子Channel的ChannelPipeline
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            //6.echoServerHandler被标注为Shareable,所以我们可以总是使用同样的实例
                            ch.pipeline().addLast(new MessageDecoder(NettyConfig.MAX_FRAME_LENGTH, NettyConfig.LENGTH_FIELD_OFFSET, NettyConfig.LENGTH_FIELD_LENGTH,
                                            NettyConfig.LENGTH_ADJUSTMENT, NettyConfig.INITIAL_BYTES_TO_STRIP))
                                    .addLast(new MessageEncoder())
                                    .addLast(new IdleStateHandler(NettyConfig.READ_IDLE_TIME_SECONDS, NettyConfig.WRITE_IDLE_TIME_SECONDS, NettyConfig.ALL_IDLE_TIME_SECONDS))
                                    .addLast(new SocketManagerHandler())
                                    .addLast(new MsgHandler());

                        }
                    });
            ChannelFuture f = null;//异步的绑定服务器，调用sync方法阻塞等待知道绑定完成
            try {
                f = b.bind().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            f.channel().closeFuture().sync();//获取channel的closeFuture,并阻塞当前线程知道它完成
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bossGroup.shutdownGracefully().sync();//关闭eventLoopGroup，释放所有的资源
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void destroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
