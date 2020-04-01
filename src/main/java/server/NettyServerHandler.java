package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 服务器端收到任何一个客户端的消息都会触发这个方法
     * 连接的客户端向服务器端发送消息，那么其他客户端都收到此消息，自己收到【自己】+消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        Channel channel = channelHandlerContext.channel();

//        channelGroup.forEach(ch -> {
//            ch.writeAndFlush(msg+"\n");
////            if(channel != ch){
////                ch.writeAndFlush(msg+" \n");
////            }else {
////                ch.writeAndFlush(msg +" \n");
////            }
//        });
        channelGroup.forEach(ch -> {
            if(channel != ch){
                ch.writeAndFlush(msg);
            }else {
                ch.writeAndFlush(msg);
            }
        });
    }

    //表示服务端与客户端连接建立
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
//
//        /**
//         * 调用channelGroup的writeAndFlush其实就相当于channelGroup中的每个channel都writeAndFlush
//         *
//         * 先去广播，再将自己加入到channelGroup中
//         */
//        channelGroup.writeAndFlush(" 【客户端】 -" +channel.remoteAddress() +" 加入\n");
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
//        channelGroup.writeAndFlush(" 【客户端】 -" +channel.remoteAddress() +" 离开\n");

        //验证一下每次客户端断开连接，连接自动地从channelGroup中删除调。
        System.out.println(channelGroup.size());
        //当客户端和服务端断开连接的时候，下面的那段代码netty会自动调用，所以不需要人为的去调用它
        channelGroup.remove(channel);
    }

    //连接处于活动状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() +" 上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() +" 下线了");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
