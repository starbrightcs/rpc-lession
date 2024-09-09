package com.starbright.interceptor;

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 自定义客户端响应拦截器
 * @author: Star Bright
 * @date: 2024/9/9 11:30
 */
public class CustomForwardingClientCallListener<RespT> extends ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT> {

	private static final Logger log = LoggerFactory.getLogger(CustomForwardingClientCallListener.class);

	protected CustomForwardingClientCallListener(ClientCall.Listener<RespT> delegate) {
		super(delegate);
	}

	/**
	 * 监听响应头的拦截动作
	 *
	 * @param headers 响应头
	 */
	@Override
	public void onHeaders(Metadata headers) {
		log.info("响应头信息回来了..., headers: {}", headers);
		super.onHeaders(headers);
	}

	/**
	 * 监听消息回来的时候进行拦截
	 *
	 * @param message 响应的数据
	 */
	@Override
	public void onMessage(RespT message) {
		log.info("响应的数据回来了..., message: {}", message);
		super.onMessage(message);
	}

}
