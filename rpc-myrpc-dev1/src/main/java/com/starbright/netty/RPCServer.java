package com.starbright.netty;

import com.starbright.User;
import com.starbright.serializer.impl.ProtoBufSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: netty服务端
 * @author: Star Bright
 * @date: 2024/9/15 18:14
 */
public class RPCServer {

	private static final Logger log = LoggerFactory.getLogger(RPCServer.class);

	public static void main(String[] args) throws InterruptedException {
		new ServerBootstrap()
				.group(new NioEventLoopGroup())
				.channel(NioServerSocketChannel.class)
				// handler：serverSocketChannel childHandler：socketChannel
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel socketChannel) throws Exception {
						socketChannel.pipeline()
								// 封帧，防止半包、粘包，我们配置了 byteBuf.writeInt() 这里就是4
								.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0))
								.addLast(new LoggingHandler())
								.addLast(new RPCMessageToMessageCodec(new ProtoBufSerializer()))
								.addLast(new ChannelInboundHandlerAdapter() {
									@Override
									public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
										User user = (User) msg;
										log.info("拿到数据 user: {}", user);
									}
								});
					}
				})
				.bind(8080);
	}

}
