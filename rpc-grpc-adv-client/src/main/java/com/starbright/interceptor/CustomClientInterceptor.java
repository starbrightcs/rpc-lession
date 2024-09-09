package com.starbright.interceptor;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 自定义拦截器
 * @author: Star Bright
 * @date: 2024/9/7 11:46
 */
public class CustomClientInterceptor implements ClientInterceptor {

	private static final Logger log = LoggerFactory.getLogger(CustomClientInterceptor.class);

	/**
	 * 拦截器
	 *
	 * @param methodDescriptor 方法描述，里面包含需要调用的 类、方法、参数
	 * @param callOptions      调用相关的参数
	 * @param channel          管道
	 * @param <ReqT>           请求参数
	 * @param <RespT>          响应参数
	 * @return clientCall
	 */
	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
		log.info("这是一个拦截器启动的处理，后续统一做了一些操作... ");
		/*
		 * 拦截器处理完成后就需要执行后续真正的操作了，那什么是真正的操作呢？
		 * 发起远端方法服务的调用，这里需要一个ClientCall进行调用。那ClientCall对象从哪里来呢？
		 * 在gRPC中服务端和客户端通信的核心是Netty，在Netty中使用什么来通信？那肯定是Channel，那就是说拿到Channel就可以拿到ClientCall了。
		 */
		// return channel.newCall(methodDescriptor, callOptions);
		/*
		 * 如果我们需要使用复杂客户端拦截器，就需要对ClientCall进行包装，不能返回原始的ClientCall了，
		 * 应该返回继承 ClientInterceptors.CheckedForwardingClientCall 的子类
		 */
		return new CustomForwardingClientClass<>(channel.newCall(methodDescriptor, callOptions));
	}

}
