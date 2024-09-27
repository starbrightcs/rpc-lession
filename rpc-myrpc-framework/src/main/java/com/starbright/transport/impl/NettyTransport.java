package com.starbright.transport.impl;

import com.starbright.RpcClientInitializer;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;
import com.starbright.transport.Transport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @description: netty网络通信
 * @author: Star Bright
 * @date: 2024/9/25 17:17
 */
public class NettyTransport implements Transport {

	private final Bootstrap bootstrap;
	private final EventLoopGroup worker;

	public NettyTransport() {
		this(1);
	}

	public NettyTransport(int workerThreads) {
		worker = new NioEventLoopGroup(workerThreads);
		bootstrap = new Bootstrap();
		bootstrap.group(worker);
		bootstrap.channel(NioSocketChannel.class);
	}

	@Override
	public Result invoke(HostAndPort hostAndPort, MethodInvokeData methodInvokeData) throws Exception {
		RpcClientInitializer rpcClientInitializer = new RpcClientInitializer(methodInvokeData);
		ChannelFuture channelFuture = bootstrap.handler(rpcClientInitializer)
				.connect(hostAndPort.getHostName(), hostAndPort.getPort())
				.sync();
		channelFuture.channel().closeFuture().sync();
		return rpcClientInitializer.getResult();
	}

	@Override
	public void close() {
		worker.shutdownGracefully();
	}

}
