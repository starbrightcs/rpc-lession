package com.starbright;

import com.starbright.codec.RPCMessageToMessageCodec;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.serializer.impl.HessianSerializer;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @description: 服务端 handler
 * @author: Star Bright
 * @date: 2024/9/24 15:15
 */
public class RpcServerProviderInitializer extends ChannelInitializer<NioSocketChannel> {

	private static final Logger log = LoggerFactory.getLogger(RpcServerProviderInitializer.class);
	/**
	 * 处理 netty 编解码、内置的 handler 通过该线程组进行处理
	 */
	private final EventLoopGroup eventLoopGroupHandler;

	/**
	 * 涉及业务的 handler 分配给该线程组
	 */
	private final EventLoopGroup eventLoopGroupService;

	/**
	 * 存放服务端提供功能的类对象，key:类全限定名 value:实现类对象
	 */
	private final Map<String, Object> exposeBeans;

	public RpcServerProviderInitializer(EventLoopGroup eventLoopGroupHandler, EventLoopGroup eventLoopGroupService, Map<String, Object> exposeBeans) {
		this.eventLoopGroupHandler = eventLoopGroupHandler;
		this.eventLoopGroupService = eventLoopGroupService;
		this.exposeBeans = exposeBeans;
	}

	@Override
	protected void initChannel(NioSocketChannel channel) throws Exception {
		log.info("init channel invoke...");
		channel.pipeline()
				// 1. 解决封帧的问题，也就是半包和粘包
				.addLast(eventLoopGroupHandler, new LengthFieldBasedFrameDecoder(1024, 6, 4))
				// 2. 日志分析
				.addLast(eventLoopGroupHandler,new LoggingHandler())
				// 3. 编解码
				.addLast(eventLoopGroupService, new RPCMessageToMessageCodec(new HessianSerializer()))
				// 4. 实际的 RPC 功能调用
				.addLast(eventLoopGroupService, new ChannelInboundHandlerAdapter() {
					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						// 得到的是 MethodInvokeData, client 用于 RPC 调用的参数
						MethodInvokeData methodInvokeData = (MethodInvokeData) msg;

						// 完成 rpc 调用
						Result result = executeTargetObject(methodInvokeData, exposeBeans);

						// 进行响应数据给 client
						ChannelFuture channelFuture = ctx.writeAndFlush(result);

						// 关闭连接,一个请求处理完成就关闭，因为 Netty 建立的是长连接
						channelFuture.addListener(ChannelFutureListener.CLOSE);
						channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
					}
				});
	}

	/*
		MethodInvokeData
			1. 调用哪个接口 com.starbright.service.UserService
			2. 哪个方法 login
			3. 参数类型
			4. 实际参数
			转换为 UserServiceImpl.login(实际参数)
		怎么获取调用的对象？
		map exposeBeans
			key: com.starbright.service.UserService
			value: UserServiceImpl
	 */
	private Result executeTargetObject(MethodInvokeData methodInvokeData, Map<String, Object> exposeBeans)  {
		log.info("execute target object {}", methodInvokeData);

		// 获取接口信息
		Class targetInterface = methodInvokeData.getTargetInterface();
		// 获取接口的实现类对象
		Object nativeObj = exposeBeans.get(targetInterface.getName());
		Result result = new Result();
		try {
			// 获取接口中需要调用的方法。需要方法名称 参数类型
			Method method = targetInterface.getDeclaredMethod(methodInvokeData.getMethodName(), methodInvokeData.getParameterTypes());
			// 进行方法的调用
			Object ret = method.invoke(nativeObj, methodInvokeData.getArgs());
			log.info("execute result {}", ret);
			result.setResultValue(ret);
		} catch (Exception e) {
			log.error("execute target object error, e: {}", e.getMessage(), e);
			result.setException(e);
		}
		return result;
	}

}
