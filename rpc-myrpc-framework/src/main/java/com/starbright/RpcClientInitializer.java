package com.starbright;

import com.starbright.codec.RPCMessageToMessageCodec;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.serializer.impl.HessianSerializer;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: client handler
 * @author: Star Bright
 * @date: 2024/9/25 17:23
 */
public class RpcClientInitializer extends ChannelInitializer<NioSocketChannel> {

	private static final Logger log = LoggerFactory.getLogger(RpcClientInitializer.class);

	private final MethodInvokeData methodInvokeData;

	@Getter
	private Result result;

	public RpcClientInitializer(MethodInvokeData methodInvokeData) {
		this.methodInvokeData = methodInvokeData;
	}

	@Override
	protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
		nioSocketChannel.pipeline()
				.addLast(new LengthFieldBasedFrameDecoder(1024, 6, 4))
				.addLast(new LoggingHandler())
				.addLast(new RPCMessageToMessageCodec(new HessianSerializer()))
				.addLast(new ChannelInboundHandlerAdapter() {

					// client 向服务器端发送请求
					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						// 调用
						log.info("client send methodInvokeData:{}", methodInvokeData);
						ChannelFuture channelFuture = ctx.writeAndFlush(methodInvokeData);
						channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
							@Override
							public void operationComplete(Future<? super Void> future) throws Exception {
								if (future.isSuccess()) {
									log.info("client send success");
								} else {
									log.error("client send error", future.cause());
								}
							}
						});

						// 异常的时候关闭连接
						channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
					}

					// 接收服务端响应的数据
					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						log.info("client receive message:{}", msg);
						result = (Result) msg;
					}
				});
	}

}
