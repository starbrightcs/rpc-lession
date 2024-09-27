package com.starbright;

import com.starbright.codec.RPCMessageToMessageCodec;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.serializer.impl.HessianSerializer;
import com.starbright.service.UserService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @description: jdk8 之后需要添加启动参数
 * @author: Star Bright
 * @date: 2024/9/24 23:20
 */
public class TestClient {

	public static void main(String[] args) throws InterruptedException {
		new Bootstrap()
				.channel(NioSocketChannel.class)
				.group(new NioEventLoopGroup())
				.handler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
						nioSocketChannel.pipeline()
								.addLast(new LengthFieldBasedFrameDecoder(1024, 6, 4))
								.addLast(new LoggingHandler())
								.addLast(new RPCMessageToMessageCodec(new HessianSerializer()))
								.addLast(new ChannelInboundHandlerAdapter() {
									@Override
									public void channelActive(ChannelHandlerContext ctx) throws Exception {
										// 调用
										MethodInvokeData methodInvokeData = MethodInvokeData.builder()
												.targetInterface(UserService.class)
												.methodName("login")
												.parameterTypes(new Class[]{String.class, String.class})
												.args(new Object[]{"starbright", "123456"})
												.build();
										ctx.writeAndFlush(methodInvokeData);
									}
								});
					}
				})
				.connect("127.0.0.1", 8081)
				.sync();
	}

}
