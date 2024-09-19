package com.starbright.netty;

import com.starbright.User;
import com.starbright.serializer.impl.ProtoBufSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @description: netty客户端
 * @author: Star Bright
 * @date: 2024/9/15 17:43
 */
public class RPCClient {

	public static void main(String[] args) throws InterruptedException {
		ChannelFuture connect = new Bootstrap()
				.group(new NioEventLoopGroup())
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
						nioSocketChannel.pipeline()
								// 封帧，防止半包、粘包，我们配置了 byteBuf.writeInt() 这里就是4
								.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4,0,0))
								// 添加日志打印
								.addLast(new LoggingHandler())
								// 添加自定义编解码器
								.addLast(new RPCMessageToMessageCodec(new ProtoBufSerializer()))
								// 服务端连接成功后发送消息
								.addLast(new ChannelInboundHandlerAdapter() {
									@Override
									public void channelActive(ChannelHandlerContext ctx) throws Exception {
										// 发送数据 user对象
										ctx.writeAndFlush(new User("starbright"));
									}
								});
					}
				})
				.connect(new InetSocketAddress("127.0.0.1", 8080));

		// 客户端连接是异步的，这里就阻塞等待
		connect.sync();
	}

}
