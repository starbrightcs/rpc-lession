package com.starbright.interceptor;

import io.grpc.ClientCall;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 复杂客户端拦截器，复制拦截请求发送的环节
 * @author: Star Bright
 * @date: 2024/9/7 13:49
 */
public class CustomForwardingClientClass<ReqT, RespT> extends ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT> {

	private static final Logger log = LoggerFactory.getLogger(CustomForwardingClientClass.class);

	protected CustomForwardingClientClass(ClientCall<ReqT, RespT> delegate) {
		super(delegate);
	}

	/**
	 * 请求开始调用时会回调这个方法。目的是为了判断这个RPC的请求是否可以被发起，可以做一些相关的业务处理
	 *
	 * @param listener 响应监听器
	 * @param metadata 元数据信息【请求头相关的信息】
	 * @throws Exception
	 */
	@Override
	protected void checkedStart(Listener<RespT> listener, Metadata metadata) throws Exception {
		log.info("发送请求数据之前的检查，可以自定义拦截处理...");
		// 真正的去发起一个gRPC请求,是否真正的发起gRPC请求取决于这个start方法是否调用
		// delegate().start(listener, metadata);
		// 自定义响应端拦截器，需要不需要对响应进行拦截，则使用上面的即可
		delegate().start(new CustomForwardingClientCallListener<>(listener), metadata);
	}

	// ========= 后面三方法根据实际业务决定是否需要实现 ==========

	/**
	 * 指定发送消息的数量
	 *
	 * @param numMessages
	 */
	@Override
	public void request(int numMessages) {
		log.info("request numMessages {}", numMessages);
		super.request(numMessages);
	}

	/**
	 * 发送消息，发送到缓冲区
	 * @param message
	 */
	@Override
	public void sendMessage(ReqT message) {
		log.info("send message {}", message);
		super.sendMessage(message);
	}

	/**
	 * 开始半连接阶段：请求消息无法发送，但是能获取响应的信息
	 */
	@Override
	public void halfClose() {
		log.info("half close");
		super.halfClose();
	}

}
