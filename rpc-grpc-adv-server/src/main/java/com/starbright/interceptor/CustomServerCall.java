package com.starbright.interceptor;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 自定义ServerCall，拦截响应
 * @author: Star Bright
 * @date: 2024/9/9 12:47
 */
public class CustomServerCall<ReqT, RespT> extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {

	private static final Logger log = LoggerFactory.getLogger(CustomServerCall.class);

	protected CustomServerCall(ServerCall<ReqT, RespT> delegate) {
		super(delegate);
	}

	/**
	 * 设置响应的消息数量
	 */
	@Override
	public void request(int numMessages) {
		log.debug("response 指定消息的数量 [request]");
		super.request(numMessages);
	}

	/**
	 * 设置响应头
	 */
	@Override
	public void sendHeaders(Metadata headers) {
		log.debug("response 设置响应头 [sendHeaders]");
		super.sendHeaders(headers);
	}

	/**
	 * 响应数据
	 */
	@Override
	public void sendMessage(RespT message) {
		log.debug("response 设置响应数据 [sendMessage], message: {}", message);
		super.sendMessage(message);
	}

	/**
	 * 关闭连接的时候调用
	 */
	@Override
	public void close(Status status, Metadata trailers) {
		log.debug("response 关闭连接 [close]");
		super.close(status, trailers);
	}

}
